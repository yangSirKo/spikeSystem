package com.ccyang.miaosha.service;

import com.ccyang.miaosha.dao.OrderDao;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.domain.SpikeOrder;
import com.ccyang.miaosha.redis.OrderKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 获取 SpikeOrder
     * @param userId
     * @param goodsId
     * @return
     */
    public SpikeOrder getSpikeOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSpikeOrderByUidGid,""+userId+"_"+goodsId,SpikeOrder.class);
    }

    public OrderInfo getOrderById(long orderId){
        return orderDao.getOrderById(orderId);
    }

    /**
     * 写入订单表、写入秒杀订单表
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsPrice(goodsVo.getSpikePrice());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrderInfo(orderInfo);

        SpikeOrder spikeOrder = new SpikeOrder();
        spikeOrder.setGoodsId(goodsVo.getId());
        spikeOrder.setOrderId(orderInfo.getId());
        spikeOrder.setUserId(user.getId());
        orderDao.insertSpikeOrder(spikeOrder);
        log.info("insert order_info & insert spike_order finish.");

        redisService.set(OrderKey.getSpikeOrderByUidGid,""+user.getId()+"_"+goodsVo.getId(),spikeOrder);
        log.info("spikeOrder put redis cache.");
        return orderInfo;
    }
}
