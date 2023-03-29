package com.bofry.databroker.core.component;

import java.util.Map;

public class EnvironmentVariableProvider implements IConfigProvider {

    private final Map<String, String> env;

    public EnvironmentVariableProvider() {
        this.env = System.getenv();
    }

    @Override
    public String get(String key) {
        return env.get(key);
    }

}
