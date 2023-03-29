package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class SubstituteFieldAssigner implements IValueAssigner {

    private String field;

    @Override
    public void init() {
        if (this.field == null) {
            throw new IllegalArgumentException();
        }
    }

    @SneakyThrows
    @Override
    public void assign(Object o, Field f) {
        Object v = o.getClass().getDeclaredField(this.field).get(o);
        if (v != null) {
            o.getClass().getDeclaredField(f.getName()).set(o, v);
        }
    }

    public String getField() {
        return this.field;
    }
}
