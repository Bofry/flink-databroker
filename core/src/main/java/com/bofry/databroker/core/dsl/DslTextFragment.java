package com.bofry.databroker.core.dsl;

import java.util.Map;

class DslTextFragment extends DslQuoteBlockFragment {
    private final DslLiteral literal;

    DslTextFragment(DslLiteralFragment f, String openDelimiter, String closeDelimiter) {
        super(f, openDelimiter, closeDelimiter);

        String value = super.value();
        literal = DslLiteral.valueOf(value);
    }

    public String text() {
        return super.value();
    }

    @Override
    public String apply(Map<String, Object> values) {
        return literal.toString();
    }
}
