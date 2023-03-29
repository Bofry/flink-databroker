package com.bofry.databroker.core;

import com.bofry.databroker.core.component.*;
import com.bofry.databroker.core.component.*;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 取得 分析 program arguments
        ArgumentResolve resolver = new ArgumentResolve(args);
        String conf = resolver.getConfigurationContext();

        // 解析Sources Config, getMap
        ConfigResolve configResolve = new ConfigResolve(conf);
        List<SourceConfiguration> sourceConfig = configResolve.getSourceConfiguration();
        List<SinkConfiguration> sinkConfig = configResolve.getSinkConfiguration();

        List<DataStream> dataStream = new ArrayList<>();

        // 建立 Source
        for (SourceConfiguration c : sourceConfig) {
            // Object
            ISourceBuilder sourceBuilder = SourceFactory.create(c.getType());
            DataStream ds = env.addSource(sourceBuilder.buildSource(c));
            dataStream.add(ds);
        }

        // 建立 Sink
        for (DataStream stream : dataStream) {
            for (SinkConfiguration c : sinkConfig) {
                ISinkBuilder sinkBuilder = SinkFactory.create(c.getType());
                sinkBuilder.buildSink(c, stream);
            }
        }
        env.execute();
    }

}