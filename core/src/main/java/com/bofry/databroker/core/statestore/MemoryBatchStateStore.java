package com.bofry.databroker.core.statestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryBatchStateStore<E> extends BatchStateStore<E> {
    private final List<String> _container = new ArrayList<>();
    private final AtomicLong _payloadSize = new AtomicLong(0);
    private final AtomicLong _idempotence = new AtomicLong(0);
    private final AtomicLong _lastPoppedIdempotence = new AtomicLong(0);

    public MemoryBatchStateStore(IPayloadFormatter formatter) {
        super(formatter);
    }

    @Override
    protected BatchStateStoreStatus infoImpl() {
        return BatchStateStoreStatus.builder()
            .idempotence(_idempotence.get())
            .payloadSize(_payloadSize.get())
            .recordCount(_container.size())
            .build();
    }

    @Override
    protected void popImpl(BatchPayloadWriter writer) {
        popImpl(writer, _idempotence.get());
    }

    @Override
    protected void popImpl(BatchPayloadWriter writer, long idempotence) {
        long length = getPopItemCount(idempotence);
        if (length > 0) {
            length = Math.min(length, _container.size());

            for (int i = 0; i < length; i++) {
                if (_container.size() == 0)
                    break;

                String entity = _container.get(0);
                writer.write(entity);

                _container.remove(0);
                _payloadSize.addAndGet(-entity.length());
            }
        }
    }

    @Override
    protected BatchStateStoreStatus putImpl(String content) {
        _container.add(content);
        long finalPayloadSize = _payloadSize.addAndGet(content.length());
        long finalIdempotenct = _idempotence.incrementAndGet();
        long finalRecordCount = _container.size();

        return BatchStateStoreStatus.builder()
            .idempotence(finalIdempotenct)
            .payloadSize(finalPayloadSize)
            .recordCount(finalRecordCount)
            .build();
    }

    private long getPopItemCount(long idempotence) {
        if (idempotence <= _idempotence.get()){
            AtomicLong last = _lastPoppedIdempotence;
            long origin = last.compareAndExchange(Math.min(last.get(), idempotence), idempotence);
            if (last.get() != origin) {
                return idempotence - origin;
            }
        }
        return 0;
    }
}
