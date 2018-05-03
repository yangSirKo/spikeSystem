package com.ccyang.miaosha.controller;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.GoodsService;
import com.ccyang.miaosha.service.OrderService;
import com.ccyang.miaosha.vo.GoodsVo;
import com.ccyang.miaosha.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/detail/{orderId}")
    @ResponseBody
    public Result<OrderDetailVo> info(MiaoshaUser user, @PathVariable("orderId")Long orderId){

        log.info(String.valueOf(orderId));
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrderInfo(orderInfo);
        log.info(orderDetailVo.toString());
        return Result.success(orderDetailVo);
    }
}
