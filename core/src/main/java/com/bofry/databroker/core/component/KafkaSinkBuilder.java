package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class KafkaSinkBuilder extends AbstractSinkBuilder {

    @Override
    protected SinkFunction createSink(SinkBuildArgs args) {
        return new KafkaSink(args.getConfiguration());
    }
}
