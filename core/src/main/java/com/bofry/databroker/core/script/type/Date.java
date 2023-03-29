package com.bofry.databroker.core.script.type;

public class Date extends java.sql.Date {

    public Date(long date) {
        super(date);
    }

    @Override
    public String toString() {
        return String.valueOf(super.getTime());
    }

}
