package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.util.Map;

public class JsonValue implements IValue {

    private final String value;

    public JsonValue(String v) {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        this.value = v;
    }

    @Override
    public String toJson() {
        return value;
    }

    @SneakyThrows
    @Override
    public Map toMap() {
        return JacksonUtils.OBJECT_MAPPER.readValue(this.value, Map.class);
    }

    @Override
    public Object toEntity(Class clazz) {
        // FIXME
        return null;
    }

}
