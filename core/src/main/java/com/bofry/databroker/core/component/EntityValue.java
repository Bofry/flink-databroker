package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.util.Map;

public class EntityValue implements IValue {

    private final Object value;

    public EntityValue(Object v) {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        this.value = v;
    }

    @SneakyThrows
    @Override
    public String toJson() {
        return JacksonUtils.toJsonString(this.value);
    }

    @SneakyThrows
    @Override
    public Map toMap() {
        if (this.value instanceof Map) {
            return (Map) this.value;
        }
        // FIXME
        return JacksonUtils.OBJECT_MAPPER.readValue(JacksonUtils.toJsonString(this.value), Map.class);
    }

    @Override
    public Object toEntity(Class clazz) {
        return value;
    }

    public static Object unWarp(EntityValue v) {
        return v.value;
    }

}
