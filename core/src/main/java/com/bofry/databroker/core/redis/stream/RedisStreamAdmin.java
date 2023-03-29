package com.bofry.databroker.core.redis.stream;

import java.util.concurrent.atomic.AtomicBoolean;

public final class RedisStreamAdmin {

    private final RedisStreamAdapter adapter;
    private final RedisStreamConnection connection;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public RedisStreamAdmin(RedisStreamAdapter adapter) {
        if (adapter == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "adapter"));

        this.adapter = adapter;
        this.connection = adapter.getConnection();
    }

    public String createConsumerGroup(RedisStreamOffset offset, String group) {
        if (offset == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "offset"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));

        checkIfClosed();
        checkIfConnectionNotReady();

        return createConsumerGroup(offset, group, null);
    }

    public String createConsumerGroup(RedisStreamOffset offset, String group, RedisStreamGroupCreateOptions opts) {
        if (offset == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "offset"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));

        checkIfClosed();
        checkIfConnectionNotReady();

        if (opts == null) {
            opts = RedisStreamGroupCreateOptions.DEFAULT;
        }
        return this.adapter.xgroupCreate(offset, group, opts);
    }

    public boolean deleteConsumerGroup(String stream, String group) {
        if (Assertion.isNullOrEmpty(stream))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "stream"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));

        checkIfClosed();
        checkIfConnectionNotReady();

        return this.adapter.xgroupDestory(stream, group);
    }

    public String setConsumerGroupOffset(RedisStreamOffset offset, String group) {
        if (offset == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "offset"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));

        checkIfClosed();
        checkIfConnectionNotReady();

        return this.adapter.xgroupSetId(offset, group);
    }

    public long deleteConsumer(String stream, String group, String consumer) {
        if (Assertion.isNullOrEmpty(stream))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "stream"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));
        if (Assertion.isNullOrEmpty(consumer))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "consumer"));

        checkIfClosed();
        checkIfConnectionNotReady();

        return this.adapter.xgroupDelConsumer(stream, group, consumer);
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
