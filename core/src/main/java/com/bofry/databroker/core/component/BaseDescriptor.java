package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.type.Date;
import com.bofry.databroker.core.script.type.Time;
import com.bofry.databroker.core.script.type.Timestamp;
import org.apache.flink.shaded.asm7.org.objectweb.asm.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BaseDescriptor implements IObjectDescriptor {

    private static final Map<String, String> DESCRIPTORS = new HashMap<>();

    static {
        DESCRIPTORS.put("boolean",   Type.getObjectType(Type.getInternalName(Boolean.class)).getDescriptor());
        DESCRIPTORS.put("string",    Type.getObjectType(Type.getInternalName(String.class)).getDescriptor());
        DESCRIPTORS.put("integer",   Type.getObjectType(Type.getInternalName(Integer.class)).getDescriptor());
        DESCRIPTORS.put("long",      Type.getObjectType(Type.getInternalName(Long.class)).getDescriptor());
        DESCRIPTORS.put("decimal",   Type.getObjectType(Type.getInternalName(BigDecimal.class)).getDescriptor());
        DESCRIPTORS.put("date",      Type.getObjectType(Type.getInternalName(Date.class)).getDescriptor());
        DESCRIPTORS.put("time",      Type.getObjectType(Type.getInternalName(Time.class)).getDescriptor());
        DESCRIPTORS.put("timestamp", Type.getObjectType(Type.getInternalName(Timestamp.class)).getDescriptor());
        DESCRIPTORS.put("json",      Type.getObjectType(Type.getInternalName(RawContent.class)).getDescriptor());
    }

    @Override
    public String get(String key) {
        return DESCRIPTORS.get(key);
    }
}
