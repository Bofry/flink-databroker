package com.bofry.databroker.core.component;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class KubernetesConfiguration implements Serializable {

    private String namespace;
    private String name;
    @JsonAlias("config_file")
    private String configFile;

}

