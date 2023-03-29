package com.bofry.databroker.core.serdes;

import com.bofry.databroker.core.component.IValueAssigner;
import com.bofry.databroker.core.component.JacksonUtils;
import com.bofry.databroker.core.component.PrimitiveAssigner;
import com.bofry.databroker.core.component.SubstituteFieldAssigner;
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

public class DefaultAssignerDeserializer extends JsonDeserializer<IValueAssigner> {

    private static final Map<String, Class> ASSIGNER_TABLE = new HashMap<>();

    static {
        ASSIGNER_TABLE.put("ref", SubstituteFieldAssigner.class);
    }

    @Override
    public IValueAssigner deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String tag = getTagName(p);
        TreeNode v = getValue(p);
        if ((tag != null && v.isObject()) || (tag == null && !v.isObject())) {
            return createAssigner(tag, v);
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

    private IValueAssigner createAssigner(String tag, TreeNode data) {
        IValueAssigner assigner;
        if (tag != null) {
            Class clazz = ASSIGNER_TABLE.get(tag);
            assigner = (IValueAssigner) JacksonUtils.OBJECT_MAPPER.convertValue(data, clazz);
            assigner.init();
            return assigner;
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
        assigner = new PrimitiveAssigner(v);
        assigner.init();
        return assigner;
    }
}
