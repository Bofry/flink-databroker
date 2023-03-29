package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public class KafkaDescriptor implements IObjectDescriptor {

    private static final Map<String, String> DESCRIPTORS = new HashMap<>();

    static {
    }

    @Override
    public String get(String key) {
        return DESCRIPTORS.get(key);
    }
}
