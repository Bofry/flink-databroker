package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.util.Map;

public class MapValue implements IValue {

    private final Map value;

    public MapValue(Map v) {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        value = v;
    }

    @SneakyThrows
    @Override
    public String toJson() {
        return JacksonUtils.toJsonString(this.value);
    }

    @Override
    public Map toMap() {
        return this.value;
    }

    @Override
    public Object toEntity(Class clazz) {
        // FIXME
        return null;
    }

}
