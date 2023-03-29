package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public abstract class Failure extends Exception implements IFailure {

    protected Failure(Throwable cause) {
        super(cause);
    }

    public Failure(String message) {
        super(message);
    }

    private final Map<String, Object> content = new HashMap<>();

    public boolean isRetryable() {
        return false;
    }

    public boolean isFailure() {
        return false;
    }

    @Override
    public Map<String, Object> getContent() {
        return this.content;
    }
}
