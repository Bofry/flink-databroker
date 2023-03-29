package com.bofry.databroker.core.statestore;

public final class BatchStateStoreStatus {
    long _idempotence;
    long _payloadSize;
    long _recordCount;

    BatchStateStoreStatus() {
    }

    public long idempotence() {
        return _idempotence;
    }

    public long payloadSize() {
        return _payloadSize;
    }

    public long recordCount() {
        return _recordCount;
    }

    public static BatchStateStoreStatusBuilder builder() {
        return new BatchStateStoreStatusBuilder();
    }
}
