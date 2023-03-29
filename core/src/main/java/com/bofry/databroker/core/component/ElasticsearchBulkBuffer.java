package com.bofry.databroker.core.component;

import com.bofry.databroker.core.statestore.*;
import com.bofry.databroker.core.statestore.*;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.Objects;

public class ElasticsearchBulkBuffer implements Serializable {

    private final ElasticsearchProperties properties;
    BatchStateStore<ElasticsearchMetadataItem> store = new MemoryBatchStateStore<>(MockSimpleFormatter.INSTANCE);

    public ElasticsearchBulkBuffer(ElasticsearchProperties properties) {
        Objects.requireNonNull(properties);

        this.properties = properties;
    }

    public Integer getCapacity() {
        return this.properties.getBulkFlushCapacity();
    }

    public Integer getMaxContentBytes() {
        return this.properties.getBulkFlushMaxSize();
    }

    @SneakyThrows
    public void write(ElasticsearchMetadataItem data) {
        Objects.requireNonNull(data);
        this.store.put(data);
    }

    /**
     * This can be thought of as analogous to Nagle's algorithm in TCP.
     */
    @SneakyThrows
    public boolean shouldFlush() {
        BatchStateStoreStatus status = this.store.info();
        return status.payloadSize() >= this.getCapacity() ||
                status.recordCount() > this.getMaxContentBytes();
    }

    public String read() {
        BatchPayload<ElasticsearchMetadataItem> payload = this.store.pop();
        String outputBuffer = payload.readAsString();
        return outputBuffer;
    }

    public boolean isEmpty() {
        BatchStateStoreStatus status = this.store.info();
        return status.recordCount() == 0 || status.payloadSize() == 0;
    }

}
