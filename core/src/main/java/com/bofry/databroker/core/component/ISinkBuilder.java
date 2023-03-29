package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.datastream.DataStream;

public interface ISinkBuilder<T> {

    void buildSink(SinkConfiguration conf, DataStream dataStream);

}
