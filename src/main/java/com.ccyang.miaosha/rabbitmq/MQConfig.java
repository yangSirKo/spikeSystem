package com.ccyang.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String DIRECT_QUEUE = "direct.queue";

    public static final String FANOUT_QUEUE1 = "fanout.queue1";
    public static final String FANOUT_QUEUE2 = "fanout.queue2";
    public static final String FANOUT_EXCHANGE = "fanout.exchange";

    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String TOPIC_KEY1 = "topic.key1";
    public static final String TOPIC_KEY2 = "topic.#";

    public static final String HEADER_QUEUE = "header.queue";
    public static final String HEADER_EXCHANGE = "header.exchange";


    /**
     * Direct 模式 交换机Exchange
     */
    @Bean
    public Queue directQueue(){
        // 一个参数是名称，，另一个表示是否持久化
        return new Queue(DIRECT_QUEUE,true);
    }

    /**---------------------------------------------*/

    /**
     * Fanout Pattern.   类似于广播一样，将消息发送给和他绑定的队列
     **/
    @Bean
    public Queue fanoutQueue1(){
        return new Queue(FANOUT_QUEUE1,true);
    }
    @Bean
    public Queue fanoutQueue2(){
        return new Queue(FANOUT_QUEUE2,true);
    }
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    /**
     * 绑定 exchange and queue
     */
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

    /**---------------------------------------------*/

    /**
     * Topic Pattern.  绑定交换机时可以做匹配。 #：表示零个或多个单词。*：表示一个单词
     **/
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    /**
     * 绑定 exchange and queue
     */
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_KEY1);  // 精确匹配, 匹配成功则发送到 TOPIC_QUEUE1队列
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_KEY2);  // 模糊匹配，匹配成功则发送到 TOPIC_QUEUE2队列
    }

    /**---------------------------------------------*/

    /**
     * Header Pattern.  交换机 Exchange
     **/
    @Bean
    public Queue headerQueue(){
        return new Queue(HEADER_QUEUE,true);
    }
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADER_EXCHANGE);
    }
    @Bean
    public Binding headerBinding(){
        Map<String,Object> map = new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();   // whereXxx() 方法代表了匹配规则

    }



}
