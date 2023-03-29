package com.bofry.databroker.core.statestore;

public final class BatchStateStoreStatusBuilder {
    private final BatchStateStoreStatus _subject = new BatchStateStoreStatus();

    public BatchStateStoreStatusBuilder() {}

    public BatchStateStoreStatusBuilder idempotence(long value) {
        _subject._idempotence = value;
        return this;
    }
    
    public BatchStateStoreStatusBuilder payloadSize(long value) {
        _subject._payloadSize = value;
        return this;
    }
    
    public BatchStateStoreStatusBuilder recordCount(long value) {
        _subject._recordCount = value;
        return this;
    }

    public BatchStateStoreStatus build() {
        return _subject;
    }
}
