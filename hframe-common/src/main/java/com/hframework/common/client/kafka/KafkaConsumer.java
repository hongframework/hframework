package com.hframework.common.client.kafka;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangquanhong on 2016/10/10.
 */
public class KafkaConsumer implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private KafkaMessageListenerContainer kafkaMessageListenerContainer;


    private int threadNum = 8;
    private int maxQueueSize = 2000;
    private ExecutorService executorService = new ThreadPoolExecutor(threadNum,
            threadNum, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(maxQueueSize),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void onMessage(final ConsumerRecord<Integer, String> record) {
        try{
            logger.info("===============processMessage===============");
            logger.info("the kafka message is arriving with topic = {}, partition = {}, key = {}, value = {}",
                    record.topic(), record.partition(), record.key(), record.value());
            //这里收到消息后，开启了一个线程来处理
            final String value = record.value();
            executorService.execute(new Runnable() {

                public void run() {
                    String msg = value;

                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //设置监听
    public void afterPropertiesSet() throws Exception {
        ContainerProperties containerProperties = kafkaMessageListenerContainer.getContainerProperties();

        if (null != containerProperties) {
            containerProperties.setMessageListener(this);
        }
    }
}