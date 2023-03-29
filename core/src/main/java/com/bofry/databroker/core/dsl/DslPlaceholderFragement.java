package com.bofry.databroker.core.dsl;

import java.util.Map;

class DslPlaceholderFragement extends DslQuoteBlockFragment {
    private final String name;

    protected DslPlaceholderFragement(DslLiteralFragment f, String openDelimiter, String closeDelimiter) {
        super(f, openDelimiter, closeDelimiter);
        this.name = super.value();
    }

    public String name() {
        return this.name;
    }

    @Override
    public String apply(Map<String, Object> values) {
        Object o = values.get(name);
        if (o != null) {
            DslLiteral literal = DslLiteral.valueOf(o);
            return literal.toString();
        }
        return null;
    }
}
