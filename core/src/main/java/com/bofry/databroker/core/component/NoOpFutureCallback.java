package com.bofry.databroker.core.component;

import org.apache.flink.cassandra.shaded.com.google.common.util.concurrent.FutureCallback;

import javax.annotation.Nullable;

public final class NoOpFutureCallback implements FutureCallback {
    @Override
    public void onSuccess(@Nullable Object o) {
        // Ignore
    }

    @Override
    public void onFailure(Throwable t) {
        // Ignore
    }
}
