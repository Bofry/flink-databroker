package com.bofry.databroker.core.redis.stream;

import java.time.Duration;
import java.util.UUID;

public final class RedisStreamConsumerProperties {
    private static final int DEFAULT_MAX_IN_FLIGHT = 8;
    private static final Duration DEFAULT_MAX_POLLING_TIMEOUT = Duration.ofMillis(100);
    private static final Duration DEFAULT_CLAIM_MIN_IDLE_TIME = Duration.ofSeconds(30);
    private static final Duration DEFAULT_IDLING_TIMEOUT = Duration.ofMillis(300);

    String group;
    String name;
    int maxInFlight;
    Duration maxPollingTimeout;
    Duration claimMinIdleTime;
    Duration idlingTimeout; // 若沒有任何訊息時等待多久
    int claimSensitivity; // Read 時取得的訊息數小於等於 n 的話, 執行 Claim
    int claimOccurrenceRate; // Read 每執行 n 次後 執行 Claim 1 次

    public RedisStreamConsumerProperties() {
        super();
    }

    public RedisStreamConsumerProperties setGroup(String value) {
        this.group = value;
        return this;
    }

    public RedisStreamConsumerProperties setName(String value) {
        this.name = value;
        return this;
    }

    public RedisStreamConsumerProperties setMaxInFlight(int value) {
        this.maxInFlight = value;
        return this;
    }

    public RedisStreamConsumerProperties setMaxPollingTimeout(Duration value) {
        this.maxPollingTimeout = value;
        return this;
    }

    public RedisStreamConsumerProperties setClaimMinIdleTime(Duration value) {
        this.claimMinIdleTime = value;
        return this;
    }

    public RedisStreamConsumerProperties setIdlingTimeout(Duration value) {
        this.idlingTimeout = value;
        return this;
    }

    public RedisStreamConsumerProperties setClaimSensitivity(int value) {
        this.claimSensitivity = value;
        return this;
    }

    public RedisStreamConsumerProperties setClaimOccurrenceRate(int value) {
        this.claimOccurrenceRate = value;
        return this;
    }

    void initialize() {
        if (Assertion.isNullOrEmpty(this.group))
            throw new RuntimeException(SR.getString(SR.RedisStreamConsumerConfig_NullOrEmptyString, "group"));

        if (Assertion.isNullOrEmpty(this.name)) {
            this.name = UUID.randomUUID().toString();
        }

        if (this.maxInFlight <= 0) {
            this.maxInFlight = DEFAULT_MAX_IN_FLIGHT;
        }

        if (this.maxPollingTimeout.toMillis() <= 0) {
            this.maxPollingTimeout = DEFAULT_MAX_POLLING_TIMEOUT;
        }

        if (this.claimMinIdleTime.toMillis() <= 0) {
            this.claimMinIdleTime = DEFAULT_CLAIM_MIN_IDLE_TIME;
        }

        if (this.idlingTimeout.toMillis() <= 0) {
            this.idlingTimeout = DEFAULT_IDLING_TIMEOUT;
        }

        if (this.claimSensitivity < 0)
            throw new RuntimeException(SR.getString(SR.RedisStreamConsumerConfig_NonNegativeInteger, "claimSensitivity"));

        if (this.claimOccurrenceRate < 0)
            throw new RuntimeException(SR.getString(SR.RedisStreamConsumerConfig_NonNegativeInteger, "claimOccurrenceRate"));
    }

    public static RedisStreamConsumerProperties create() {
        return new RedisStreamConsumerProperties();
    }
}
