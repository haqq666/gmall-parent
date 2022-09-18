package com.atguigu.gmall.rabbit;


import lombok.extern.slf4j.Slf4j;;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/9/14 18:08
 */
@Slf4j
@EnableRabbit
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class AppRabbitConfiguration {

    @Bean
    RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer,
                                  ConnectionFactory connection){
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template,connection);

        //与服务器连接
        template.setConfirmCallback(((CorrelationData correlationData,
                                      boolean ack,
                                      String cause) ->{
            if (!ack){
                log.error("消息投递失败，已经保存到数据库，消息 ：{}" , correlationData );
            }
        }));

        //与队列连接
        template.setReturnCallback((Message message,
                                    int replyCode,
                                    String replyText,
                                    String exchange,
                                    String routingKey)->{
            log.error("消息投递到队列失败，已经保存到数据库，消息：{}",message);

        });

        //重试 //使用默认的 重试3次
        template.setRetryTemplate(new RetryTemplate());

        return template;
    }

    @Bean
    public RabbitService rabbitService(){
        return new RabbitService();
    }

}
