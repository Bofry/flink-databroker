package com.bofry.databroker.core.redis.stream;

final class SR {
    static final String ArgumentMustNotNull = "specified argument '%s' must not be null.";
    static final String ArgumentNullOrEmptyString = "specified argument '%s' cannot be null or an empty string.";
    static final String ArgumentMustNonNegativeInteger = "specified argument '%s' must not be a negative integer.";
    static final String ArgumentMustNotEmpty = "specified argument '%s' must not be empty.";

    static final String RedisStreamMessageId_ArgInvalidFormat = "specified argument '%s' is not a valid Redis stream message id format.";
    static final String RedisStreamAdapter_UnsupportedRedisVersion = "specified '%s' cannot manipulate STREAM.";
    static final String RedisStreamConnection_Closed = "specified RedisStreamConnection object '%s' has been closed.";
    static final String RedisStreamConnection_NotReady = "specified RedisStreamConnection object '%s' is not ready.";
    static final String RedisStreamProducer_Closed = "specified RedisStreamProducer object '%s' has been closed.";
    static final String RedisStreamConsumer_Closed = "specified RedisStreamConsumer object '%s' has been closed.";
    static final String RedisStreamVersion_InvalidFormat = "specified version '%s' is not a valid format.";
    static final String RedisStreamConsumerConfig_NullOrEmptyString = "specified RedisStreamConsumerConfig '%s' cannot be null or an empty string.";
    static final String RedisStreamConsumerConfig_NonNegativeInteger = "specified RedisStreamConsumerConfig '%s' must not be a negative integer.";
    static final String RedisStreamGroupCreateOptions_Immutable = "specified RedisStreamGroupCreateOptions object is immutable.";

    static String getString(String format, Object... args) {
        return String.format(format, args);
    }
}
