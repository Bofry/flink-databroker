package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;

public class MysqlDescriptor implements IObjectDescriptor {

    private static final Map<String, String> descriptors = new HashMap<>();

    @Override
    public String get(String key) {
        return descriptors.get(key);
    }
}
