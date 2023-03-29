package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public class SourceFactory {

    private static final Map<String, ISourceBuilder> SOURCE_ENGINE_CONTAINER = new HashMap<>();

    static {
        SOURCE_ENGINE_CONTAINER.put("kafka", new KafkaSourceBuilder());
        SOURCE_ENGINE_CONTAINER.put("redis", new RedisSourceBuilder());
    }

    public static ISourceBuilder create(String sourceType) {
        return SOURCE_ENGINE_CONTAINER.get(sourceType);
    }
}
