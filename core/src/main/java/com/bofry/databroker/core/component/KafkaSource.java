package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class KafkaSource extends RichParallelSourceFunction<InputStreamContext> {

    private KafkaConsumer<String, String> kafkaConsumer;
    private final String id;
    private final String type;
    private final String route;
    private final String topic;
    private final Properties props;
    private final Map<String, MappingField> mappingTable;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public KafkaSource(SourceConfiguration conf) {
        this.id = UUID.randomUUID().toString();
        this.type = conf.getType();
        this.route = conf.getRoute();
        this.topic = conf.getConfig().get("topic").toString();
        this.mappingTable = conf.getMappingTable();
        this.props = JacksonUtils.OBJECT_MAPPER.convertValue(conf.getConfig().get("properties"), Properties.class);
    }

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
        if (this.kafkaConsumer == null) {
            synchronized (this) {
                if (this.kafkaConsumer == null) {
                    super.open(parameters);
                    // FIXME 目前寫死 kafka 只接受 String 的 key, value
                    this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                    this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                    // 解決 Client.id 會有重複問題。
                    this.props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());

                    this.kafkaConsumer = new KafkaConsumer<>(this.props);
                    this.kafkaConsumer.subscribe(Collections.singletonList(topic));
                }
            }
        }
    }

    @Override
    public void run(SourceContext ctx) {
        try {
            while (!closed.get()) {
                if (closed.get()) {
                    break;
                }
                ConsumerRecords<String, String> records;
                synchronized (this.kafkaConsumer) {
                    records = kafkaConsumer.poll(Duration.ofSeconds(10));
                }
                for (ConsumerRecord<String, String> record : records) {
                    if (closed.get()) {
                        break;
                    }
                    String topic = record.topic();
                    Integer partition = record.partition();
                    Long offset = record.offset();
                    String key = record.key();
                    String value = record.value();
                    Headers headers = record.headers();

                    // 其他資訊塞入 Map metadata;
                    Map<String, Object> metadata = new HashMap<>();
                    MetadataHelper.setTopic(metadata, topic);
                    MetadataHelper.setPartition(metadata, partition);
                    MetadataHelper.setOffset(metadata, offset);
                    MetadataHelper.setKey(metadata, key);
                    MetadataHelper.setHeaders(metadata, headers);

                    InputStreamContext data = InputStreamContext.builder()
                            .id(this.id)
                            .route(this.route)
                            .mappingTable(this.mappingTable)
                            .metadata(metadata)
                            .source(new JsonValue(value))
                            .contentSourceType(this.type)
                            .rawContent(record)
                            .build();
                    ctx.collect(data);
                }
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } finally {
            this.kafkaConsumer.close();
        }

    }

    @Override
    public void cancel() {
        log.info("kafka source cancel.");
        this.close();
    }

    @SneakyThrows
    @Override
    public void close() {
        if(this.closed.compareAndSet(false, true)){
            log.info("kafka source close.");
            if (kafkaConsumer != null) {
                kafkaConsumer.wakeup();
                super.close();
            }
        }
//        if (!this.closed.get()) {
//            log.info("kafka source close.");
//            this.closed.set(true);
//            if (kafkaConsumer != null) {
//                kafkaConsumer.wakeup();
//                super.close();
//            }
//        }
    }

}
