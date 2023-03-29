package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class StdoutSinkBuilder extends AbstractSinkBuilder {

    @Override
    protected SinkFunction createSink(SinkBuildArgs args) {
        return new StdoutSink(args.getConfiguration());
    }
}
