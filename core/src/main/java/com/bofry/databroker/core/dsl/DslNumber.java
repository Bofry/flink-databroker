package com.bofry.databroker.core.dsl;

// TODO: implement serializable
public final class DslNumber extends DslValue {
    private final Number value;

    public DslNumber(Number value) {
        if (value == null)
            throw new IllegalArgumentException(String.format("specified argument cannot '%s' be null", "value"));

        this.value = value;
    }

    @Override
    public DslValueType getType() {
        return DslValueType.Number;
    }

    @Override
    public DslLiteral toLiteral() {
        String literal = this.value.toString();
        return new DslLiteral(literal);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    public static Number valueOf(DslNumber n) {
        return n.value;
    }
}
