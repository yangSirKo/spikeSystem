package com.ccyang.miaosha.rabbitmq;

import com.ccyang.miaosha.Result.CodeMsg;
import com.ccyang.miaosha.Result.Result;
import com.ccyang.miaosha.domain.MiaoshaUser;
import com.ccyang.miaosha.domain.OrderInfo;
import com.ccyang.miaosha.domain.SpikeOrder;
import com.ccyang.miaosha.redis.GoodsKey;
import com.ccyang.miaosha.redis.RedisService;
import com.ccyang.miaosha.service.GoodsService;
import com.ccyang.miaosha.service.OrderService;
import com.ccyang.miaosha.service.SpikeService;
import com.ccyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static final Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    SpikeService spikeService;

    @Autowired
    GoodsService goodsService;

    /**
     * spike goods
     * @param message
     */
    @RabbitListener(queues = MQConfig.SPIKE_QUEUE)
    public void spikeReceive(String message){
        log.info("receive spike message:" + message);
        SpikeMessage sm = RedisService.StringToBean(message,SpikeMessage.class);
        MiaoshaUser user = sm.getUser();
        Long goodsId = sm.getGoodsId();

        // 判断商品库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() <= 0){
            return ;
        }
        // 判断是否秒杀到了
        SpikeOrder order = orderService.getSpikeOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            //把预减库存加回去, 因为入队之前预减库存了
            redisService.incr(GoodsKey.spikeGoodsStock,""+goodsId);
            return ;
        }
        // 减库存 写入订单表 写入秒杀订单表
        spikeService.spike(user, goodsVo);
    }


    /** --------------- exchange test -------------- */

    /**
     * Direct Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.DIRECT_QUEUE)
    public void directReceive(String message){
        log.info("receive direct message:" + message);
    }

    /** ----------------------------- */

    /**
     * fanout Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.FANOUT_QUEUE1)
    public void fanoutReceive1(String message){
        log.info("receive fanout1 message:" + message);
    }
    /**
     * fanout Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.FANOUT_QUEUE2)
    public void fanoutReceive2(String message){
        log.info("receive fanout2 message:" + message);
    }

    /** ----------------------------- */

    /**
     * topic Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void topicReceive1(String message){
        log.info("receive topic1 message:" + message);
    }
    /**
     * topic Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void topicReceive2(String message){
        log.info("receive topic2 message:" + message);
    }

    /** ----------------------------- */

    /**
     * Header Pattern.
     * @param message
     */
    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void topicHeader(byte[] message){
        log.info("receive topic1 message:" + new String(message));
    }

}
