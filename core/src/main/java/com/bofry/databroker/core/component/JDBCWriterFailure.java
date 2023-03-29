package com.bofry.databroker.core.component;

public class JDBCWriterFailure extends Failure {

    public JDBCWriterFailure(Exception exception) {
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
        // TODO JDBC 目前沒有實作 判斷是否可以 Retry
        return false;
    }

}
