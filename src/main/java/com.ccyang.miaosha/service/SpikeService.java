package com.ccyang.miaosha.service;

import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.domain.SpikeOrder;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.redis.SpikeKey;
import com.ccyang.miaosha.util.MD5Util;
import com.ccyang.miaosha.util.UUIDUtil;
import com.ccyang.miaosha.vo.GoodsVo;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SpikeService {

    private static final Logger log = LoggerFactory.getLogger(SpikeService.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    /**
     * 减库存 写入订单表 写入秒杀订单表
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo spike(MiaoshaUser user, GoodsVo goodsVo) {

        // 减库存
        boolean b = goodsService.reduceStock(goodsVo);
        if(b){
            // 写入订单表 写入秒杀订单表
            return orderService.createOrder(user,goodsVo);
        }else{
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }



    /**
     * 查看秒杀结果
     * orderId : spike success
     *  -1 : spike fail
     *  0 : in queue
     */
    public long spikeResult(long userId, long goodsId) {
        SpikeOrder order = orderService.getSpikeOrderByUserIdGoodsId(userId,goodsId);
        if(order != null){
            return order.getOrderId();
        }else{
            boolean b = getGoodsOver(goodsId);
            if(b){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SpikeKey.isGoodsOver,""+goodsId,true);  // true: goodsOver
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SpikeKey.isGoodsOver,""+goodsId);
    }

    public String getSpikePath(MiaoshaUser user, long goodsId) {
        String path =  MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SpikeKey.getSpikePath,""+user.getId()+"_"+goodsId,path);
        return path;
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {

        if(user == null || path == null){
            return false;
        }
        String oidPath = redisService.get(SpikeKey.getSpikePath,""+user.getId()+"_"+goodsId,String.class);
        return oidPath.equals(path);
    }

    public BufferedImage createCodeImg(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0){
            return null;
        }
        int width = 80;
        int height = 32;

        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);

        g.setColor(Color.black);
        g.drawRect(0,0,width-1,height-1);

        Random random = new Random();
        for(int i=0; i<50; i++){

            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawOval(x,y,0,0);
        }

        String verifyCode = createVerifyCode(random);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();

        // 验证码存入 redis
        int rnd = calc(verifyCode);
        redisService.set(SpikeKey.getSpikeVerifyCode,""+user.getId()+","+goodsId,rnd);

        // 输出图片
        return image;
    }

    private static final char[] opts = {'+','-','*'};
    private String createVerifyCode(Random random) {

        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        char opt1 = opts[random.nextInt(3)];
        char opt2 = opts[random.nextInt(3)];

        String str = ""+ num1 + opt1 + num2 + opt2 + num3 ;
        return str;
    }

    private static int calc(String verifyCode) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine sc = manager.getEngineByName("JavaScript");
            return (Integer)sc.eval(verifyCode);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(calc("1+3"));

    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <= 0){
            return false;
        }
        Integer oldCode = redisService.get(SpikeKey.getSpikeVerifyCode,""+user.getId()+","+goodsId,Integer.class);
        if(oldCode == null || oldCode - verifyCode != 0){
            return false;
        }
        redisService.delete(SpikeKey.getSpikeVerifyCode,""+user.getId()+","+goodsId);
        return true;
    }
}
