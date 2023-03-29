package com.bofry.databroker.core.redis.stream;

public abstract class LettuceStreamConnection extends RedisStreamConnection {

    protected abstract LettuceStreamClient<String, String> getStreamClient();
}
