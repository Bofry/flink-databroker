package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;

@Data
@Builder
final class SinkBuildArgs implements Serializable {

    private SinkConfiguration configuration;
    private DataStream dataStream;

}
