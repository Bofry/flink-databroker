package com.bofry.databroker.core.component;

public class CassandraWriterFailure extends Failure {

    public CassandraWriterFailure(Exception exception) {
        super(exception);
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isRetryable() {
        // TODO Cassandra 目前沒有實作 判斷是否可以 Retry
        return false;
    }

}

