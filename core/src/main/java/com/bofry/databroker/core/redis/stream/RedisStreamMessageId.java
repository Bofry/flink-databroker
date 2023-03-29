package com.bofry.databroker.core.redis.stream;

import java.io.Serializable;

// see: https://github.com/redisson/redisson/blob/b050966dd375805fc042a5e1d22ac0c6b0a1e58a/redisson/src/main/java/org/redisson/api/StreamMessageId.java
public final class RedisStreamMessageId implements Serializable {
    static final String TOKEN_ASTERISK_ID = "*";
    static final String TOKEN_ZERO_ID = "0";
    static final String TOKEN_LAST_DELIVERED_ID = "$";

    // using by known ID token "*", "0", and "$"
    static final long KNOWN_MESSAGE_ID = -1;

    // known IDs
    public static final RedisStreamMessageId ASTERISK = new RedisStreamMessageId(KNOWN_MESSAGE_ID, 0, true);
    public static final RedisStreamMessageId ZERO = new RedisStreamMessageId(KNOWN_MESSAGE_ID, 0, true);
    public static final RedisStreamMessageId LAST_DELIVERED = new RedisStreamMessageId(KNOWN_MESSAGE_ID, 0, true);

    private final long id0;
    private final long id1;

    public RedisStreamMessageId(long id0) {
        this(id0, 0L, false);
    }

    public RedisStreamMessageId(long id0, long id1) {
        this(id0, id1, false);
    }

    private RedisStreamMessageId(long id0, long id1, boolean skipCheck) {
        if (!skipCheck) {
            if (id0 < 0L)
                throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNonNegativeInteger, "id0"));
            if (id1 < 0L)
                throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNonNegativeInteger, "id1"));
        }

        this.id0 = id0;
        this.id1 = id1;
    }

    public boolean isKnown() {
        return (id0 == KNOWN_MESSAGE_ID);
    }

    public long getId0() {
        return id0;
    }

    public long getId1() {
        return id1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id0 ^ (id0 >>> 32));
        result = prime * result + (int) (id1 ^ (id1 >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RedisStreamMessageId other = (RedisStreamMessageId) obj;
        if (id0 != other.id0)
            return false;
        if (id1 != other.id1)
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (this == ASTERISK) {
            return TOKEN_ASTERISK_ID;
        }
        if (this == ZERO) {
            return TOKEN_ZERO_ID;
        }
        if (this == LAST_DELIVERED) {
            return TOKEN_LAST_DELIVERED_ID;
        }

        return id0 + "-" + id1;
    }

    public static RedisStreamMessageId parse(String id) {
        if (id == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "id"));

        String[] parts = id.toString().split("-");

        if (parts.length == 1) {
            switch (id) {
                case TOKEN_ASTERISK_ID:
                    return ASTERISK;
                case TOKEN_ZERO_ID:
                    return ZERO;
                case TOKEN_LAST_DELIVERED_ID:
                    return LAST_DELIVERED;
            }
        } else if (parts.length == 2) {
            try {
                long id0 = Long.valueOf(parts[0]);
                long id1 = Long.valueOf(parts[1]);

                return new RedisStreamMessageId(id0, id1);
            } catch (Exception ex) {
                throw new IllegalArgumentException(SR.getString(SR.RedisStreamMessageId_ArgInvalidFormat, "id"), ex);
            }
        }

        throw new IllegalArgumentException(SR.getString(SR.RedisStreamMessageId_ArgInvalidFormat, "id"));
    }
}
