package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public abstract class AbstractSinkBuilder implements ISinkBuilder {

    public void buildSink(SinkConfiguration conf, DataStream dataStream) {
        SinkBuildArgs args = SinkBuildArgs.builder()
                .configuration(conf)
                .dataStream(dataStream)
                .build();
        SinkFunction sink = this.createSink(args);
        dataStream.addSink(sink);
    }

    protected abstract SinkFunction createSink(SinkBuildArgs args);

}
