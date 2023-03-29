package com.bofry.databroker.core.redis.stream;

public final class RedisStreamGroupCreateOptions {
    public static final RedisStreamGroupCreateOptions DEFAULT = new RedisStreamGroupCreateOptions(true);

    private final boolean immutable;

    boolean mkstream = false;

    public RedisStreamGroupCreateOptions() {
        this(false);
    }

    private RedisStreamGroupCreateOptions(boolean immutable) {
        this.immutable = immutable;
    }

    public boolean isImmutable() {
        return this.immutable;
    }

    public RedisStreamGroupCreateOptions mkstream(boolean value) {
        if (this.immutable)
            throw new RuntimeException(SR.getString(SR.RedisStreamGroupCreateOptions_Immutable));

        this.mkstream = value;
        return this;
    }

    public static RedisStreamGroupCreateOptions create() {
        return new RedisStreamGroupCreateOptions();
    }
}
