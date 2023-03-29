package com.bofry.databroker.core.statestore;

import java.util.concurrent.atomic.AtomicBoolean;

public final class BatchPayloadWriter {
    private final BatchPayload<?> _payload;
    private final AtomicBoolean _closed = new AtomicBoolean(false);

    public BatchPayloadWriter(BatchPayload<?> payload) {
        _payload = payload;
    }

    public void write(String entity) {
        if (_closed.get()) 
            throw new RuntimeException("the BatchPayloadWriter has been closed.");

        _payload.writeContent(entity);
    }

    public void close() {
        _closed.compareAndSet(false, true);
    }
}
