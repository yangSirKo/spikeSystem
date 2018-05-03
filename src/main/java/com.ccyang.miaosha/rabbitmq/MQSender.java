package com.ccyang.miaosha.rabbitmq;

import com.ccyang.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static final Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendSpikeMessage(SpikeMessage sm){
        String msg = RedisService.BeanToString(sm);
        log.info(msg);
        amqpTemplate.convertAndSend(MQConfig.SPIKE_QUEUE,msg);
    }






    /**
     * use Direct Pattern.  RabbitMQ default, no need exchange.
     * @param message
     */
    public void sendDirect(Object message){
        String msg = RedisService.BeanToString(message);
        log.info("send direct message:" + msg );
        amqpTemplate.convertAndSend(MQConfig.DIRECT_QUEUE,msg);
    }

    /**
     * use Direct Pattern
     * @param message
     */
    public void sendFanout(Object message){
        String msg = RedisService.BeanToString(message);
        log.info("send fanout message:" + msg );
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }

    /**
     * use Topic Pattern
     * @param message
     */
    public void sendTopic(Object message){
        String msg = RedisService.BeanToString(message);
        log.info("send Topic message:" + msg );
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");  // 可以匹配到 topic.# and topic.key1
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");  // 可以匹配到 topic.#
    }

    /**
     * use Header Pattern
     * @param message
     */
    public void sendHeader(Object message){
        String msg = RedisService.BeanToString(message);
        log.info("send Header message:" + msg );

        MessageProperties mp = new MessageProperties();
        mp.setHeader("header1","value1");
        mp.setHeader("header2","value2");   // 与 配置中的map完全匹配，因为那边是 whereAll()方法
        Message message1 = new Message(msg.getBytes(),mp);
        amqpTemplate.convertAndSend(MQConfig.HEADER_EXCHANGE,"",message1);  // 可以匹配到 topic.# and topic.key1
    }



}
