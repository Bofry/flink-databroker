package com.bofry.databroker.core.dsl;

public abstract class DslValue {
    public static final DslNull NULL = DslNull.VALUE;
    public static final DslBoolean TRUE = DslBoolean.TRUE;
    public static final DslBoolean FALSE = DslBoolean.FALSE;

    public abstract DslValueType getType();
    public abstract DslLiteral toLiteral();

    public static DslValue valueOf(Object o) {
        if (o == null) {
            return NULL;
        }

        if (o instanceof DslValue) {
            return (DslValue)o;
        }

        if (o instanceof Boolean) {
            if (Boolean.TRUE.equals(o)) {
                return TRUE;
            }
            return FALSE;
        }

        if (o instanceof Number) {
            Number v = (Number)o;
            return new DslNumber(v);
        }

        if (o instanceof String) {
            return new DslString(o.toString());
        }

        throw new UnsupportedOperationException(String.format("specified type '%s' cannot convert to DslValue", o.getClass()));
    }
}
