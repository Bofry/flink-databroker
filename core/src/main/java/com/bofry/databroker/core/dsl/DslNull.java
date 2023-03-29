package com.bofry.databroker.core.dsl;

// TODO: implement serializable
public final class DslNull extends DslValue {
    public static final DslNull VALUE = new DslNull();

    private DslNull() {
        super();
    }

    @Override
    public DslValueType getType() {
        return DslValueType.Null;
    }

    @Override
    public DslLiteral toLiteral() {
        return DslLiteral.NULL;
    }

    @Override
    public String toString() {
        return null;
    }
}
