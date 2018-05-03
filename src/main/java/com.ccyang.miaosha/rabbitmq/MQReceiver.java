package com.ccyang.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static final Logger log = LoggerFactory.getLogger(MQReceiver.class);

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
