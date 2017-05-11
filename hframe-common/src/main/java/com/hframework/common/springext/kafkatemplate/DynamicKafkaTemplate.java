package com.hframework.common.springext.kafkatemplate;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * 重写一个动态的kafkaTemplate
 * 注意：DynamicKafkaTemplate与DynamicKafkaProducerFactory需要同时使用
 * Created by zhangquanhong on 2016/10/11.
 */
public class DynamicKafkaTemplate<K, V> extends org.springframework.kafka.core.KafkaTemplate<K, V> implements KafkaOperations<K, V> {

    private DynamicKafkaProducerFactory<K, V> defaultProducerFactory;
    private  boolean autoFlush;
    private volatile ProducerListener<K, V> producerListener = new LoggingProducerListener<K, V>();

    private volatile Map<String, Producer<K, V>> producers = new HashMap<String, Producer<K, V>>();

    public DynamicKafkaTemplate(DynamicKafkaProducerFactory<K, V> producerFactory) {
        this(producerFactory, false);
    }

    public DynamicKafkaTemplate(DynamicKafkaProducerFactory<K, V> producerFactory, boolean autoFlush) {
        super(producerFactory, autoFlush);
        defaultProducerFactory = producerFactory;
        this.autoFlush = autoFlush;
    }

    public ListenableFuture<SendResult<K, V>> send(String servers, String topic, V data) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<K, V>(topic, data);
        return doSend(servers, producerRecord);
    }

    public ListenableFuture<SendResult<K, V>> send(String servers, String topic, K key, V data) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<K, V>(topic, key, data);
        return doSend(servers, producerRecord);
    }

    public ListenableFuture<SendResult<K, V>> send(String servers, String topic, int partition, V data) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<K, V>(topic, partition, null, data);
        return doSend(servers, producerRecord);
    }

    public ListenableFuture<SendResult<K, V>> send(String servers, String topic, int partition, K key, V data) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<K, V>(topic, partition, key, data);
        return doSend(servers, producerRecord);
    }

    /**
     * Send the producer record.
     * @param producerRecord the producer record.
     * @return a Future for the {@link RecordMetadata}.
     */
    protected ListenableFuture<SendResult<K, V>> doSend(String servers, final ProducerRecord<K, V> producerRecord) {
        getTheProducer(servers);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Sending: " + producerRecord);
        }
        final SettableListenableFuture<SendResult<K, V>> future = new SettableListenableFuture<SendResult<K, V>>();
        getTheProducer(servers).send(producerRecord, new Callback() {

            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception == null) {
                    future.set(new SendResult<K, V>(producerRecord, metadata));
                    if (DynamicKafkaTemplate.this.producerListener != null
                            && DynamicKafkaTemplate.this.producerListener.isInterestedInSuccess()) {
                        DynamicKafkaTemplate.this.producerListener.onSuccess(producerRecord.topic(),
                                producerRecord.partition(), producerRecord.key(), producerRecord.value(), metadata);
                    }
                }
                else {
                    future.setException(new KafkaProducerException(producerRecord, "Failed to send", exception));
                    if (DynamicKafkaTemplate.this.producerListener != null) {
                        DynamicKafkaTemplate.this.producerListener.onError(producerRecord.topic(),
                                producerRecord.partition(), producerRecord.key(), producerRecord.value(), exception);
                    }
                }
            }

        });
        if (this.autoFlush) {
            flush();
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Sent: " + producerRecord);
        }
        return future;
    }

    private Producer<K, V> getTheProducer(String servers) {
        if(!producers.containsKey(servers)) {
            synchronized (this) {
                if(!producers.containsKey(servers)) {
                    producers.put(servers, createProducer(servers));
                }
            }
        }
        return producers.get(servers);
    }

    private Producer<K, V> createProducer(String servers) {
        return defaultProducerFactory.createProducer(servers);
    }

    public void setProducerListener(ProducerListener<K, V> producerListener) {
        this.producerListener = producerListener;
    }
}
