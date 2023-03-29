package com.bofry.databroker.core.component;

import org.apache.kafka.common.errors.TimeoutException;

public class KafkaWriterFailure extends Failure {

    public KafkaWriterFailure(Exception exception) {
        super(exception);
    }

    @Override
    public boolean isRetryable() {
        Throwable t = this.getCause();
        if (t != null) {
            return canRetryByException((Exception) t);
        }
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    private boolean canRetryByException(Exception exception) {
        return exception instanceof TimeoutException;
    }

}
