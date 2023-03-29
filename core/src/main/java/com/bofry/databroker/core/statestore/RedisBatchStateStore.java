package com.bofry.databroker.core.statestore;

public class RedisBatchStateStore<E> extends BatchStateStore<E> {
    private RedisBatchStateStoreProvider _provider;

    private long _db;
    private String _namespace;

    public RedisBatchStateStore(IPayloadFormatter formatter) {
        super(formatter);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected BatchStateStoreStatus infoImpl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void popImpl(BatchPayloadWriter writer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void popImpl(BatchPayloadWriter writer, long idempotence) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected BatchStateStoreStatus putImpl(String content) {
        // TODO Auto-generated method stub
        return null;
    }
}
