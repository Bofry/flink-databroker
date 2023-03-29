package com.bofry.databroker.core.component;

import org.apache.flink.shaded.asm7.org.objectweb.asm.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CassandraDescriptor implements IObjectDescriptor {

    private static final Map<String, String> DESCRIPTORS = new HashMap<>();

    static {
        DESCRIPTORS.put("map",             Type.getObjectType(Type.getInternalName(Map.class)).getDescriptor());

        DESCRIPTORS.put("string[]",  "[" + Type.getObjectType(Type.getInternalName(String.class)).getDescriptor());
        DESCRIPTORS.put("short[]",   "[" + Type.getObjectType(Type.getInternalName(Short.class)).getDescriptor());
        DESCRIPTORS.put("integer[]", "[" + Type.getObjectType(Type.getInternalName(Integer.class)).getDescriptor());
        DESCRIPTORS.put("long[]",    "[" + Type.getObjectType(Type.getInternalName(Long.class)).getDescriptor());
        DESCRIPTORS.put("float[]",   "[" + Type.getObjectType(Type.getInternalName(Float.class)).getDescriptor());
        DESCRIPTORS.put("double[]",  "[" + Type.getObjectType(Type.getInternalName(Double.class)).getDescriptor());
        DESCRIPTORS.put("boolean[]", "[" + Type.getObjectType(Type.getInternalName(Boolean.class)).getDescriptor());
        DESCRIPTORS.put("decimal[]", "[" + Type.getObjectType(Type.getInternalName(BigDecimal.class)).getDescriptor());
    }

    @Override
    public String get(String key) {
        return DESCRIPTORS.get(key);
    }
}
