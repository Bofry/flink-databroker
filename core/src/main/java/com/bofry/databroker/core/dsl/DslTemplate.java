package com.bofry.databroker.core.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DslTemplate {
    static final char[] TEXT_DELIMITER = new char[]{'"'};
    static final char[] TEXT_BLOCK_DELIMITER = new char[]{'"', '"', '"'};
    static final char[] OPEN_PLACEHOLDER_DELIMITER = "%{".toCharArray();
    static final char[] CLOSE_PLACEHOLDER_DELIMITER = "}".toCharArray();

    final List<IDslFragment> fragments = new ArrayList<>();
    private final StringBuffer buffer = new StringBuffer();

    DslTemplate() {
        super();
    }

    public String apply(Map<String, Object> values) {
        StringBuffer buffer = new StringBuffer();
        List<IDslFragment> fragments = this.fragments;
        for (int i = 0; i < fragments.size(); i++) {
            IDslFragment f = fragments.get(i);
            String v = f.apply(values);
            buffer.append(v);
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return this.buffer.toString();
    }

    StringBuffer getBuffer() {
        return this.buffer;
    }

    public static DslTemplate load(String input, DslTemplateLoader loader) throws Exception {
        return loader.load(input);
    }
}
