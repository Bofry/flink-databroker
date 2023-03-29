package com.bofry.databroker.core.script.type;

public class Time extends java.sql.Time {

    public Time(long time) {
        super(time);
    }

    @Override
    public String toString() {
        return String.valueOf(super.getTime());
    }

}
