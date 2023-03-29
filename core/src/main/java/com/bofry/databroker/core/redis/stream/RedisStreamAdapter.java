package com.bofry.databroker.core.redis.stream;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;

public abstract class RedisStreamAdapter {
    private final RedisStreamConnection connection;

    public RedisStreamAdapter(RedisStreamConnection connection) {
        if (connection == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "connection"));
        if (connection.isClosed())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_Closed, connection.getClass()));
        if (!connection.isConnected())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_NotReady, connection.getClass()));

        this.connection = connection;
    }

    public final RedisStreamConnection getConnection() {
        return this.connection;
    }

    public abstract String xadd(String stream, String id, Map<String, String> content);

    public abstract Iterator<RedisStreamMessage> xclaim(String stream, String group, String consumer, Duration minIdleTime, int limit, int pendingFetchingSize);

    public abstract Iterator<RedisStreamMessage> xreadgroup(String group, String consumer, RedisStreamOffset[] offsets, Duration timeout, int limit);

    public abstract long xack(String stream, String group, String... messageIds);

    public abstract long xdel(String stream, String... messageIds);

    public abstract String xgroupCreate(RedisStreamOffset offset, String group, RedisStreamGroupCreateOptions opts);

    public abstract boolean xgroupDestory(String stream, String group);

    public abstract String xgroupSetId(RedisStreamOffset offset, String group);

    public abstract long xgroupDelConsumer(String stream, String group, String consumer);

    public abstract Iterator<RedisStreamMessage> xrange(String stream, String startId, String endId);
}
