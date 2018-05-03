package com.ccyang.miaosha.controller;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.access.AccessLimit;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.domain.SpikeOrder;
import com.ccyang.miaosha.rabbitmq.MQSender;
import com.ccyang.miaosha.rabbitmq.SpikeMessage;
import com.ccyang.miaosha.redis.AccessKey;
import com.ccyang.miaosha.redis.GoodsKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.GoodsService;
import com.ccyang.miaosha.service.OrderService;
import com.ccyang.miaosha.service.SpikeService;
import com.ccyang.miaosha.util.MD5Util;
import com.ccyang.miaosha.util.UUIDUtil;
import com.ccyang.miaosha.vo.GoodsVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.http.io.BufferInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/spike")
public class SpikeController implements InitializingBean{

    private static final Logger log = LoggerFactory.getLogger(SpikeController.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SpikeService spikeService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化后立即执行该方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return ;
        }
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.spikeGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }


    /**
     * use page static cache
     *
     * GET POST
     * Get 是幂等的， <a href="/delete?id=1">删除</a>， 这是极其错误的。搜索引擎可能会不知不觉替你删了数据
     * Post是非幂等的
     * @return
     */
    @RequestMapping(value="/{path}/do_spike" ,method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSpike(Model model, MiaoshaUser user,
                                   @PathVariable("path")String path,
                                   @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 判断路径
        boolean checkP = spikeService.checkPath(user,goodsId,path);
        if(!checkP){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // 是否已经库存 < 0
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.SPIKE_OVER);
        }
        // 预减库存
        long stock = redisService.decr(GoodsKey.spikeGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SPIKE_OVER);
        }
        // 判断是否秒杀到了
        SpikeOrder order = orderService.getSpikeOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            //把预减库存加回去
            redisService.incr(GoodsKey.spikeGoodsStock,""+goodsId);
            return Result.error(CodeMsg.REPEAT_SPIKE);
        }
        // 入队
        SpikeMessage sm = new SpikeMessage();
        sm.setUser(user);
        sm.setGoodsId(goodsId);
        mqSender.sendSpikeMessage(sm);

        return Result.success(0);  // 0 表示排队中

//        // 判断商品库存
//        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
//        if(goodsVo.getStockCount() <= 0){
//            return Result.error(CodeMsg.SPIKE_OVER);
//        }
//        // 判断商品是否被秒杀过
//        SpikeOrder order = orderService.getSpikeOrderByUserIdGoodsId(user.getId(),goodsId);
//        if(order != null){
//            return Result.error(CodeMsg.REPEAT_SPIKE);
//        }
//        // 减库存 写入订单表 写入秒杀订单表
//        OrderInfo orderInfo = spikeService.spike(user, goodsVo);
//        return Result.success(orderInfo);
    }

    /**
     *  处理客户端轮询
     *  orderId : spike success
     *  -1 : spike fail
     *  0 : in queue
     */
    @RequestMapping(value="/result")
    @ResponseBody
    public Result<Long> result(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        log.info("SpikeController..." + goodsId + "");
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 查询是否生成订单
        long res = spikeService.spikeResult(user.getId(),goodsId);
        return Result.success(res);
    }

    /**
     * get spike path
     * @return
     */
    @AccessLimit(seconds = 5,maxCount = 5, needLogin = true)
    @RequestMapping(value="/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSpikePath(MiaoshaUser user,
                                       @RequestParam("verifyCode")Integer verifyCode,
                                       @RequestParam("goodsId")long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //判断验证码
        boolean checkVC = spikeService.checkVerifyCode(user,goodsId,verifyCode);
        if(!checkVC){
            return Result.error(CodeMsg.VERITY_ERROR);
        }
        String path = spikeService.getSpikePath(user,goodsId);
        return Result.success(path);
    }

    /**
     * get verify Code Image
     * @return
     */
    @RequestMapping(value="/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response, Model model, MiaoshaUser user,
                                        @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = spikeService.createCodeImg(user,goodsId);

        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SPIKE_FAIL);
        }
    }
}
