package com.bofry.databroker.core.component;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public interface ISourceBuilder {

    SourceFunction<InputStreamContext> buildSource(SourceConfiguration conf);

}
