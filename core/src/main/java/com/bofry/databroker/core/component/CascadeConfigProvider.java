package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CascadeConfigProvider implements IConfigProvider {

    private final List<IConfigProvider> configProviders = new ArrayList<>();

    public void addConfigProvider(IConfigProvider p) {
        this.configProviders.add(p);
    }

    @Override
    public String get(String key) {
        String value;
        for (IConfigProvider p : this.configProviders) {
            value = p.get(key);
            if (value != null) {
                return value;
            }
        }
        throw new IllegalArgumentException();
    }

}
