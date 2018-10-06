package com.hframework.common.client.kafka;


import com.hframework.common.springext.kafkatemplate.DynamicKafkaTemplate;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by zhangquanhong on 2016/10/10.
 */
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public ListenableFuture<SendResult<Integer, String>> sendMessage(String topic, String data) {
        logger.info("the message is to be send by kafka is : topic = {}, data = {}", topic, data);
//        kafkaTemplate.setDefaultTopic(topic);
        return kafkaTemplate.send(topic, data);
    }

    public ListenableFuture<SendResult<Integer, String>> sendMessage(String topic, int key, String data) {
        logger.info("the message is to be send by kafka is : topic = {}, key = {}, data = {}", topic, key, data);
        return kafkaTemplate.send(topic, (Integer) null, key, data);
    }

    public ListenableFuture sendMessage(String server, String topic, String data) {
        logger.info("the message is to be send by kafka is : server = {}, topic = {}, data = {}", server, topic, data);
        if (kafkaTemplate instanceof DynamicKafkaTemplate) {
            DynamicKafkaTemplate template = (DynamicKafkaTemplate) kafkaTemplate;
            return template.send(server,topic, data);
        }else {
            logger.warn("can't build [{}] 's producer : " +
                    "this kafkaTemplate must be the com.hframework.ext.template.DynamicKafkaTemplate instance " +
                    ",current is {} instance !",server, kafkaTemplate.getClass());
        }
        return null;
    }

    public ListenableFuture sendMessage(String server, String topic, int key, String data) {
        logger.info("the message is to be send by kafka is : server = {}, topic = {}, key = {}, data = {}", server, topic, key, data);
        if (kafkaTemplate instanceof DynamicKafkaTemplate) {
            DynamicKafkaTemplate template = (DynamicKafkaTemplate) kafkaTemplate;
            return template.send(server,topic, key, data);
        }else {
            logger.warn("can't build [{}] 's producer : " +
                    "this kafkaTemplate must be the com.hframework.ext.template.DynamicKafkaTemplate instance " +
                    ",current is {} instance !",server, kafkaTemplate.getClass());
        }
        return null;
    }

    public void getTopicDescribe(String server, String topic) {
        String[] options = new String[]{
                "--describe",
                "--zookeeper",
                server,
                "--topic",
                topic,
        };
        TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(options);
        ZkUtils zkUtils = ZkUtils.apply(server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        try {
            TopicCommand.describeTopic(zkUtils, opts);
        }finally {
            zkUtils.close();
        }
    }

    public void createTopic(String server, String topic, int partitions, int replicationFactor) {
        String[] options = new String[]{
                "--create",
                "--zookeeper",
                server,
                "--partitions",
                String.valueOf(partitions),
                "--topic",
                topic,
                "--replication-factor",
                String.valueOf(replicationFactor),
        };
        TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(options);
        ZkUtils zkUtils = ZkUtils.apply(server, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        try {
            TopicCommand.createTopic(zkUtils, opts);
        }finally {
            zkUtils.close();
        }
    }

}
