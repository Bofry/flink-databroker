package com.bofry.databroker.core.component;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class ElasticsearchWriterFailure extends Failure {

    private int statusCode;

    public ElasticsearchWriterFailure(int statusCode) {
        super("httpStatusCode:" + statusCode);
        this.statusCode = statusCode;
    }

    public ElasticsearchWriterFailure(Exception exception) {
        super(exception);
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isRetryable() {
        if (statusCode > 0) {
            return canRetryByStatusCode(statusCode);
        }
        Throwable t = this.getCause();
        if (t != null) {
            return canRetryByException((Exception) t);
        }
        return false;
    }

    /**
     * The statusCode is 429 or between 500 and 599 but 501 is not included.
     */
    private boolean canRetryByStatusCode(int statusCode) {
        if (statusCode == HttpStatusExtension.SC_TOO_MANY_REQUESTS) {
            return true;
        }
        // if (statusCode == HttpStatusExtension.SC_NOT_IMPLEMENTED) {
        //     return false;
        // }
        if (statusCode != HttpStatusExtension.SC_NOT_IMPLEMENTED &&
                500 <= statusCode && statusCode < 600) {
            return true;
        }
        // if (400 <= statusCode && statusCode < 500) {
        //     return false;
        // }
        // if (200 <= statusCode && statusCode < 300) {
        //     return false;
        // }
        return false;
    }

    private boolean canRetryByException(Exception exception) {
        if (exception instanceof SocketTimeoutException) {
            return true;
        }
        if (exception instanceof ConnectException) {
            return true;
        }
        return false;
    }

}

