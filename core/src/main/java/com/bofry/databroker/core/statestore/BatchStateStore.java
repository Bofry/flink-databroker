package com.bofry.databroker.core.statestore;

public abstract class BatchStateStore<E> {
    private final IPayloadFormatter _formatter;

    public BatchStateStore(IPayloadFormatter formatter) {
        super();

        _formatter = formatter;
    }

    public final BatchStateStoreStatus info() {
        return infoImpl();
    }

    public final BatchPayload<E> pop(long idempotence) {
        BatchPayload<E> payload = new BatchPayload<>(_formatter);
        BatchPayloadWriter writer = new BatchPayloadWriter(payload);

        popImpl(writer, idempotence);

        writer.close();
        return payload;
    }

    public final BatchPayload<E> pop() {
        BatchPayload<E> payload = new BatchPayload<>(_formatter);
        BatchPayloadWriter writer = new BatchPayloadWriter(payload);

        popImpl(writer);

        writer.close();
        return payload;
    }

    public final BatchStateStoreStatus put(E entity) {
        String content = _formatter.marshal(entity);

        return putImpl(content);
    }

    protected abstract BatchStateStoreStatus infoImpl();
    protected abstract void popImpl(BatchPayloadWriter writer);
    protected abstract void popImpl(BatchPayloadWriter writer, long idempotence);
    protected abstract BatchStateStoreStatus putImpl(String content);

    protected IPayloadFormatter getPayloadFormatter() {
        return _formatter;
    }
}
