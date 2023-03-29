package com.bofry.databroker.core.dsl;

// TODO: implement serializable
public final class DslLiteral {
    public static final DslLiteral NULL = new DslLiteral("null");
    public static final DslLiteral TRUE = new DslLiteral("true");
    public static final DslLiteral FALSE = new DslLiteral("false");

    private final String value;

    public DslLiteral(String value) {
        if (value == null)
            throw new IllegalArgumentException(String.format("specified argument cannot '%s' be null", "value"));

        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static DslLiteral valueOf(Object o) {
        if (o instanceof DslLiteral) {
            return (DslLiteral)o;
        }

        DslValue value = DslValue.valueOf(o);
        return value.toLiteral();
    }
}
