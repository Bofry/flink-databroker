package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

public class KafkaSourceBuilder implements ISourceBuilder {

    @Override
    public RichParallelSourceFunction<InputStreamContext> buildSource(SourceConfiguration conf) {
        return new KafkaSource(conf);
    }

}
