package com.bofry.databroker.core.redis.stream;

import java.io.Serializable;

public final class RedisStreamOffset implements Serializable {
    public static final String NEVER_DELIVERED_OFFSET = ">";
    public static final String ZERO_OFFSET = "0";

    private final String stream;
    private final String offset;

    RedisStreamOffset(String stream, String offset) {
        if (Assertion.isNullOrEmpty(stream))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "stream"));
        if (Assertion.isNullOrEmpty(offset))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "offset"));

        this.stream = stream;
        this.offset = offset;
    }

    public String getOffset() {
        return offset;
    }

    public String getStream() {
        return stream;
    }

    public static RedisStreamOffset at(String stream, String offset) {
        return from(stream, offset);
    }

    public static RedisStreamOffset from(String stream, String offset) {
        return new RedisStreamOffset(stream, offset);
    }

    public static RedisStreamOffset fromNeverDelivered(String stream) {
        return new RedisStreamOffset(stream, NEVER_DELIVERED_OFFSET);
    }

    public static RedisStreamOffset fromZero(String stream) {
        return new RedisStreamOffset(stream, ZERO_OFFSET);
    }
}
