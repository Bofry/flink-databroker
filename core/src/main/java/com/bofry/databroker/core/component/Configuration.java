package com.bofry.databroker.core.component;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Configuration {

    String namespace;
    KubernetesConfiguration kubernetes;
    List<SourceConfiguration> source;
    List<SinkConfiguration> sink;

}
