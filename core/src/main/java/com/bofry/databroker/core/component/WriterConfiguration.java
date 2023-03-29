package com.bofry.databroker.core.component;

import lombok.Data;

import java.util.Map;

@Data
public class WriterConfiguration implements IConfiguration {

    private String type;
    private Map<String, Object> config;

}
