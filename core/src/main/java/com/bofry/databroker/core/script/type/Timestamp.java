package com.bofry.databroker.core.script.type;

public class Timestamp extends java.sql.Timestamp {

    public Timestamp(long time) {
        super(time);
    }

    @Override
    public String toString() {
        return String.valueOf(super.getTime());
    }
}
