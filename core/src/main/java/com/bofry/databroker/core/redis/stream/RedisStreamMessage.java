package com.bofry.databroker.core.redis.stream;

import java.io.Serializable;
import java.util.Map;

public abstract class RedisStreamMessage implements Serializable {
    public abstract String getStream();

    public abstract String getId();

    public abstract Map<String, String> getBody();

    public abstract boolean commit();

    public abstract boolean expel();

    public boolean isValid() {
        return getId() != null;
    }

    public boolean commitAndExpel() {
        if (commit()) {
            return expel();
        }
        return false;
    }
}
