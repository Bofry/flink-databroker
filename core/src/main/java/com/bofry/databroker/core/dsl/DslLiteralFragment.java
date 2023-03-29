package com.bofry.databroker.core.dsl;

import java.util.Map;

class DslLiteralFragment implements IDslFragment {
    private final StringBuffer value;
    private final int offset;
    private final int length;

    DslLiteralFragment(StringBuffer buffer, int offset, int length) {
        this.value = buffer;
        this.offset = offset;
        this.length = length;
    }

    protected StringBuffer getBuffer() {
        return this.value;
    }

    public int offset() {
        return this.offset;
    }

    public int length() {
        return this.length;
    }

    @Override
    public String apply(Map<String, Object> values) {
        return this.toString();
    }

    @Override
    public String toString() {
        if (this.value == null) {
            return "";
        }
        if (this.value.length() < this.offset) {
            return "";
        }
        int last = offset + length;
        if (this.value.length() < last) {
            last = this.value.length();
        }
        return this.value.substring(offset, last);
    }
}
