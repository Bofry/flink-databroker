package com.bofry.databroker.core.redis.stream;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class RedisStreamProducer {
    private final RedisStreamAdapter adapter;
    private final RedisStreamConnection connection;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public RedisStreamProducer(RedisStreamAdapter adapter) {
        if (adapter == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "adapter"));

        this.adapter = adapter;
        this.connection = adapter.getConnection();
    }

    public String produce(String stream, RedisStreamMessageId id, Map<String, String> content) {
        return produce(stream, id.toString(), content);
    }

    public String produce(String stream, String id, Map<String, String> content) {
        if (Assertion.isNullOrEmpty(stream))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "stream"));
        if (Assertion.isNullOrEmpty(id))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "id"));
        if (content == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "content"));
        if (Assertion.isEmpty(content))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotEmpty, "content"));

        checkIfClosed();
        checkIfConnectionNotReady();

        return this.adapter.xadd(stream, id, content);
    }

    public String produce(String stream, Map<String, String> content) {
        return produce(stream, RedisStreamMessageId.TOKEN_ASTERISK_ID, content);
    }

    public void close() {
        this.closed.compareAndSet(false, true);
    }

    private void checkIfClosed() {
        if (this.closed.get())
            throw new RuntimeException(SR.getString(SR.RedisStreamProducer_Closed, this.getClass()));
    }

    private void checkIfConnectionNotReady() {
        if (this.connection.isClosed())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_Closed, this.connection.getClass()));
        if (!this.connection.isConnected())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_NotReady, this.connection.getClass()));
    }
}
