package com.bofry.databroker.core.component;

import lombok.Data;

import java.util.Map;

@Data
public class RetryConfiguration {

    private String type;
    private Map<String, Object> config;

}
