package com.hframework.common.springext.kafkatemplate;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 注意：DynamicKafkaTemplate与DynamicKafkaProducerFactory需要同时使用
 * Created by zhangquanhong on 2016/10/11.
 */
public class DynamicKafkaProducerFactory<K, V> extends DefaultKafkaProducerFactory<K, V> implements ProducerFactory<K, V>, Lifecycle, DisposableBean {

    private final Map<String, Object> configs;

    private Serializer<K> keySerializer;

    private Serializer<V> valueSerializer;


    public DynamicKafkaProducerFactory(Map<String, Object> configs) {
        this(configs, null, null);
    }

    public DynamicKafkaProducerFactory(Map<String, Object> configs, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        super(configs, keySerializer, valueSerializer);
        this.configs = new HashMap(configs);
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public synchronized Producer<K, V> createProducer(String servers) {
        Object originServers = configs.get("bootstrap.servers");
        configs.put("bootstrap.servers", servers);
        Producer<K, V> producer = createProducer();
        configs.put("bootstrap.servers", originServers);
        return producer;
    }

    protected KafkaProducer<K, V> createKafkaProducer() {
        return new KafkaProducer<K, V>(this.configs, this.keySerializer, this.valueSerializer);
    }

    public void setKeySerializer(Serializer<K> keySerializer) {
        this.keySerializer = keySerializer;
        super.setKeySerializer(keySerializer);
    }

    public void setValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
        super.setValueSerializer(valueSerializer);
    }
}
