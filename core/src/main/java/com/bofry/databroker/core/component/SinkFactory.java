package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public class SinkFactory {

    public static final Map<String, ISinkBuilder> SINK_BUILDER_CONTAINER = new HashMap<>();

    static {
        SINK_BUILDER_CONTAINER.put("stdout",        new StdoutSinkBuilder());
        SINK_BUILDER_CONTAINER.put("elasticsearch", new ElasticsearchSinkBuilder());
        SINK_BUILDER_CONTAINER.put("mysql",         new MysqlSinkBuilder());
        SINK_BUILDER_CONTAINER.put("postgresql",    new PostgresqlSinkBuilder());
        SINK_BUILDER_CONTAINER.put("sqlite",        new SQLiteSinkBuilder());
        SINK_BUILDER_CONTAINER.put("cassandra",     new CassandraSinkBuilder());
        SINK_BUILDER_CONTAINER.put("kafka",         new KafkaSinkBuilder());
        SINK_BUILDER_CONTAINER.put("redis",         new RedisSinkBuilder());
    }

    public static ISinkBuilder create(String type) {
        return SINK_BUILDER_CONTAINER.get(type);
    }

}
