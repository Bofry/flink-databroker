package com.bofry.databroker.core.redis.stream;

public final class NopRedisStreamMessageHandler implements IRedisStreamMessageHandler {
    public static final NopRedisStreamMessageHandler INSTANCE;

    static {
        INSTANCE = new NopRedisStreamMessageHandler();
    }

    public NopRedisStreamMessageHandler() {
    }

    @Override
    public void process(RedisStreamMessage message) {
        // ignored
    }
}
