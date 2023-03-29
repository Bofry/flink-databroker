package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Properties;
import java.util.UUID;

@Log4j2
public class KafkaDocumentWriter extends AbstractDocumentWriter implements IFailureDocumentWriter {

    private volatile KafkaProducer<String, String> producer;
    private final String topic;
    private final KafkaKeyEvaluator kafkaKeyEvaluator;
    private final Properties props;
    private final IAgent agent;

    public KafkaDocumentWriter(IAgent agent) {
        this.agent = agent;
        IConfiguration conf = agent.getConfiguration();
        this.topic = conf.getConfig().get("topic").toString();
        if (conf.getConfig().get("message_key") != null) {
            this.kafkaKeyEvaluator = new KafkaJRubyKeyEvaluator(conf.getConfig().get("message_key").toString());
        } else {
            this.kafkaKeyEvaluator = new KafkaDummyKeyEvaluator();
        }
        this.props = JacksonUtils.OBJECT_MAPPER.convertValue(conf.getConfig().get("properties"), Properties.class);
    }

    @Override
    public void write(OutputStreamContext ctx) {
        String key = null;
        String value = null;
        // Document writer
        try {
            IValue entity = ctx.getEntity();
            if (entity instanceof EntityValue) {
                key = this.kafkaKeyEvaluator.eval(EntityValue.unWarp((EntityValue) entity));
            }
            value = entity.toJson();

            // Writer
            this.getProducer().send(new ProducerRecord<>(
                    this.topic,
                    key,
                    value));
            this.getProducer().flush();

        } catch (JsonProcessingException e) {
            // TODO 補充處理說明
            // TODO 秀 source 或是 value
            this.throwFailure(e);
        } catch (Exception e) {
            // TODO 補充處理說明
            Failure f = new KafkaWriterFailure(e);
            FailureHelper.setKey(f.getContent(), key);
            FailureHelper.setValue(f.getContent(), value);
            FailureHelper.setSource(f.getContent(), ctx.getSource().toString());
            this.throwFailure(f);
        }
    }

    @Override
    public void write(FailureContext f) {
        String key;
        String value;
        try {
            switch (f.getAction()) {
                case RETRY:
                    key = FailureHelper.getKey(f.getContent());
                    value = FailureHelper.getValueToString(f.getContent());
                    break;
                case Fail:
                    key = FailureHelper.getKey(f.getContent());
                    value = FailureHelper.getValueToString(f.getContent());
                    if (value == null) {
                        value = FailureHelper.getSourceToString(f.getContent());
                    }
                    break;
                default:
                    return;
            }
            this.getProducer().send(new ProducerRecord<>(
                    this.topic,
                    key,
                    value));
            this.getProducer().flush();
        } catch (Exception e) {
            // TODO 秀 source 或是 value
            this.throwFailure(e);
        }
    }

    // FIXME 取得方式不太好，等待修改
    public KafkaProducer<String, String> getProducer() {
        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    // FIXME key.serializer 和 value.serializer 是否要給與設定，目前為寫死都使用 String。
                    this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
                    this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
                    this.props.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
                    this.producer = new KafkaProducer<>(props);
                }
            }
        }
        return this.producer;
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }
}
