package com.bofry.databroker.core.dsl;

// TODO: implement serializable
public final class DslBoolean extends DslValue {
    public static final DslBoolean TRUE  = new DslBoolean(true);
    public static final DslBoolean FALSE = new DslBoolean(false);

    private final boolean value;

    private DslBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public DslValueType getType() {
        return DslValueType.Boolean;
    }

    @Override
    public DslLiteral toLiteral() {
        if (this.value == true) {
            return DslLiteral.TRUE;
        }
        return DslLiteral.FALSE;
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    public static boolean valueOf(DslBoolean b) {
        return b.value;
    }
}
