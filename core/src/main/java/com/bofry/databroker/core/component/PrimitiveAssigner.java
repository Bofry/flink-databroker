package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.type.Date;
import com.bofry.databroker.core.script.type.Time;
import com.bofry.databroker.core.script.type.Timestamp;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class PrimitiveAssigner implements IValueAssigner {

    private final Object _value;

    public PrimitiveAssigner(Object v) {
        this._value = v;
    }

    @Override
    public void init() {
    }

    @SneakyThrows
    @Override
    // TODO 補型別轉換的 Test Case
    public void assign(Object o, Field f) {
        Object v = this._value;
        if (v != null ) {
            // TODO 優化整體判斷
            if(f.getType() == Timestamp.class){
                if (v instanceof Long) {
                    v = new Timestamp((Long) v);
                } else if (v instanceof Number) {
                    v = new Timestamp(Long.parseLong(v.toString()));
                } else {
                    // TODO simple date format
                    // if value is number string, Long.parse.
                    try {
                        Long l = Long.parseLong(v.toString());
                        v = new Timestamp(l);
                    } catch (NumberFormatException e) {
                        throw new UnsupportedOperationException();
                    }
                    // if value is data format, eg ISO-8601...
                    throw new UnsupportedOperationException();
                }
            }
            if(f.getType() == Date.class){
                if (v instanceof Long) {
                    v = new Date((Long) v);
                } else if (v instanceof Number) {
                    v = new Date(Long.parseLong(v.toString()));
                } else {
                    // TODO simple date format
                    // if value is number string, Long.parse.
                    try {
                        Long l = Long.parseLong(v.toString());
                        v = new Date(l);
                    } catch (NumberFormatException e) {
                        throw new UnsupportedOperationException();
                    }
                    // if value is data format, eg ISO-8601...
                    throw new UnsupportedOperationException();
                }
            }
            if(f.getType() == Time.class){
                if (v instanceof Long) {
                    v = new Time((Long) v);
                } else if (v instanceof Number) {
                    v = new Time(Long.parseLong(v.toString()));
                } else {
                    // TODO simple date format
                    // if value is number string, Long.parse.
                    try {
                        Long l = Long.parseLong(v.toString());
                        v = new Time(l);
                    } catch (NumberFormatException e) {
                        throw new UnsupportedOperationException();
                    }
                    // if value is data format, eg ISO-8601...
                    throw new UnsupportedOperationException();
                }
            }
            // TODO 各個型別轉換處理，有噴錯慢慢補。
            if(f.getType() == Long.class) {
                if(v instanceof Integer){
                    v = new Long((Integer) v);
                }
            }
        }

        o.getClass().getDeclaredField(f.getName()).set(o, v);
    }

    public Object value() {
        return this._value;
    }

}
