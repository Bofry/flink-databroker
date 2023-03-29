package com.bofry.databroker.core.redis.stream;

public interface IRedisStreamMessageHandler {
    void process(RedisStreamMessage message) throws Exception;
}
