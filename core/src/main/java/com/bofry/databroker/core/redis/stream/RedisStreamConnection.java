package com.bofry.databroker.core.redis.stream;

public abstract class RedisStreamConnection {

    public abstract void connect();

    public abstract void close();

    protected abstract boolean isConnected();

    protected abstract boolean isClosed();
}
