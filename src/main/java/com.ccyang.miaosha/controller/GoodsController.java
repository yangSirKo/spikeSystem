package com.ccyang.miaosha.controller;

import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.redis.GoodsKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.GoodsService;
import com.ccyang.miaosha.service.MiaoshaUserService;
import com.ccyang.miaosha.vo.GoodsDetailVo;
import com.ccyang.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 4.29 添加页面缓存 & URL 缓存
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver viewResolver;
    @Autowired
    ApplicationContext applicationContext;

    /**
     * 页面级缓存 ， 分页时，每次只多缓存一两页
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, MiaoshaUser user){
        model.addAttribute("user", user);

        // page Cache
        //1. from cache get html
        String html = redisService.get(GoodsKey.goodsList,"",String.class); // key: GoodsList:kl
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        // 业务逻辑
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
//      return "goods_list";

        //2. 手动渲染
        SpringWebContext swc = new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applicationContext);
        html = viewResolver.getTemplateEngine().process("goods_list",swc);
        // write html into cache
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.goodsList,"",html);
        }
        return html;
    }

    /**
     * 处理页面静态化
     * @return VO 对象
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail2(MiaoshaUser user, @PathVariable("goodsId")long goodsId){

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int skipeStatus = 0;   // spike status
        int remainSeconds = 0;  // remain ... seconds

        if(now < startAt){ // skipe no start, 倒计时
            skipeStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);

        }else if(now > endAt){ // skipe already end.
            skipeStatus = 1;
            remainSeconds = -1;
        }else{  // skiping.
            skipeStatus = 2;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetail = new GoodsDetailVo();
        goodsDetail.setGoodsVo(goods);
        goodsDetail.setMiaoshaUser(user);
        goodsDetail.setRemainSeconds(remainSeconds);
        goodsDetail.setSkipeStatus(skipeStatus);

        return Result.success(goodsDetail);
    }

    /**
     * URL 级页面缓存
     * @return
     */
    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,
                          Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId){
        model.addAttribute("user", user);

        // URL Cache. explain： based on different url cache different page
        // from cache take html
        String html = redisService.get(GoodsKey.goodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int skipeStatus = 0;
        int remainSeconds = 0;  // remain ... seconds

        if(now < startAt){ // skipe no start, 倒计时
            skipeStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){ // skipe already end.
            skipeStatus = 1;
            remainSeconds = -1;
        }else{  // skiping.
            skipeStatus = 2;
            remainSeconds = 0;
        }
        model.addAttribute("skipeStatus",skipeStatus);
        model.addAttribute("remainSeconds",remainSeconds);
//      return "goods_detail";

        // 手动渲染
        SpringWebContext swc = new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applicationContext);
        html = viewResolver.getTemplateEngine().process("goods_detail",swc);
        if(!StringUtils.isEmpty(html)){
            // write cache
            redisService.set(GoodsKey.goodsDetail,""+goodsId,html);
            log.info("GoodsDetail Cache ...");
        }
        return html;
    }

}
