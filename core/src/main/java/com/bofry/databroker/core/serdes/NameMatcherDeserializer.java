package com.bofry.databroker.core.serdes;

import com.bofry.databroker.core.component.ExactMatcher;
import com.bofry.databroker.core.component.INameMatcher;
import com.bofry.databroker.core.component.JacksonUtils;
import com.bofry.databroker.core.component.RegexMatcher;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.TreeNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.BooleanNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.NullNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.NumericNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NameMatcherDeserializer extends JsonDeserializer<INameMatcher> {

    private static final Map<String, Class> NAME_MATCHER_TABLE = new HashMap<>();

    static {
        NAME_MATCHER_TABLE.put("regex", RegexMatcher.class);
    }

    @Override
    public INameMatcher deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String tag = getTagName(p);
        TreeNode v = getValue(p);
        if ((tag != null && v.isObject()) || (tag == null && !v.isObject())) {
            return createNameMatcher(tag, v);
        }
        // TODO new ConfigException 去包
        throw new IllegalArgumentException();
    }

    private String getTagName(JsonParser p) throws IOException {
        Object t = p.getTypeId();
        if (t != null) {
            return t.toString();
        }
        return null;
    }

    private TreeNode getValue(JsonParser p) throws IOException {
        return p.readValueAsTree();
    }

    private INameMatcher createNameMatcher(String tag, TreeNode data) {
        INameMatcher matcher;
        if (tag != null) {
            Class clazz = NAME_MATCHER_TABLE.get(tag);
            matcher = (INameMatcher) JacksonUtils.OBJECT_MAPPER.convertValue(data, clazz);
            matcher.init();
            return matcher;
        }

        Object v;
        if (data instanceof NumericNode) {
            v = ((NumericNode) data).numberValue();
        } else if (data instanceof TextNode) {
            v = ((TextNode) data).textValue();
        } else if (data instanceof BooleanNode) {
            v = ((BooleanNode) data).booleanValue();
        } else if (data instanceof NullNode) {
            v = null;
        } else {
            throw new UnsupportedOperationException();
        }

        matcher = new ExactMatcher(v);
        matcher.init();
        return matcher;
    }
}
