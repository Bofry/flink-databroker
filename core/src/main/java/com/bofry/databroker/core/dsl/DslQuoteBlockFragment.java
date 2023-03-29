package com.bofry.databroker.core.dsl;

import java.util.Map;

class DslQuoteBlockFragment implements IDslFragment {
    private final DslLiteralFragment fragment;
    private final String openDelimiter;
    private final String closeDelimiter;

    protected DslQuoteBlockFragment(DslLiteralFragment f, String openDelimiter, String closeDelimiter) {
        this.fragment = f;
        this.openDelimiter = openDelimiter;
        this.closeDelimiter = closeDelimiter;
    }

    public int offset() {
        return this.fragment.offset();
    }

    public int length() {
        return this.fragment.length();
    }

    public String openDelimiter() {
        return this.openDelimiter;
    }

    public String closeDelimiter() {
        return this.closeDelimiter;
    }

    protected String value() {
        String literal = this.fragment.toString();
        int beginAt = this.openDelimiter.length();
        int endAt = literal.length() - this.closeDelimiter.length();
        return literal.substring(beginAt, endAt);
    }

    @Override
    public String apply(Map<String, Object> values) {
        return this.fragment.apply(values);
    }

    @Override
    public String toString() {
        return this.fragment.toString();
    }
}
