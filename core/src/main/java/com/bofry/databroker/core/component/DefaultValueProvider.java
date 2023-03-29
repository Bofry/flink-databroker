package com.bofry.databroker.core.component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DefaultValueProvider {

    private final Map<String, IValueAssigner> assignerTable = new HashMap<>();

    public DefaultValueProvider(Map<String, MappingField> mapping) {
        this.init(mapping);
    }

    private void init(Map<String, MappingField> mapping) {
        mapping.forEach((k, v) -> {
            if (v.getDefaultValueAssigner() != null) {
                this.assignerTable.put(v.getName(), v.getDefaultValueAssigner());
            }
        });
        this.assignerTable.forEach((k, v) -> {
            if (v instanceof SubstituteFieldAssigner) {
                SubstituteFieldAssigner a = ((SubstituteFieldAssigner) v);
                if (k.equals(a.getField())) {
                    assignerTable.remove(k);
                    return;
                }
                if (mapping.get(a.getField()) == null) {
                    throw new IllegalArgumentException();
                }
                if (!mapping.get(k).getType().equals(mapping.get(a.getField()).getType())) {
                    throw new ClassCastException();
                }
            }
        });
    }

    public void fill(Object model) {
        this.assignerTable.forEach((k, v) -> {
            try {
                Field f = model.getClass().getDeclaredField(k);
                if (f.get(model) == null) {
                    v.assign(model, f);
                }
            } catch (Exception e) {
                // Throwables.throwIfUnchecked(e);
                throw new RuntimeException(e);
            }
        });
    }

}
