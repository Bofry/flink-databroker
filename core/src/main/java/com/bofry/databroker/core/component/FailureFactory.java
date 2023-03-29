package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public class FailureFactory {

    public static final Map<String, IWriterBuilder> RETRY_WRITER_TABLE = new HashMap<>();

    static {
        RETRY_WRITER_TABLE.put("kafka",         new KafkaWriterBuilder());
        RETRY_WRITER_TABLE.put("redis",         new RedisWriterBuilder());
    }

    public static IWriterBuilder create(String type) {
        return RETRY_WRITER_TABLE.get(type);
    }

}
