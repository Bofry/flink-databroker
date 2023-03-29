package com.bofry.databroker.core.component;

import org.apache.flink.shaded.asm7.org.objectweb.asm.AnnotationVisitor;
import org.apache.flink.shaded.asm7.org.objectweb.asm.ClassWriter;
import org.apache.flink.shaded.asm7.org.objectweb.asm.FieldVisitor;
import org.apache.flink.shaded.asm7.org.objectweb.asm.Type;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

public class StdModel extends Model implements Serializable {

    public StdModel(String className, Map<String, MappingField> modelMapping, Map<String, MappingField> srcMapping, IObjectDescriptor descriptor) throws Exception {
        super(className, modelMapping, srcMapping, descriptor);
    }

//    @Override
//    protected String getFieldName(String name) {
//        return UUID.randomUUID().toString();
//    }

    @Override
    protected void setClassAnnotation(ClassWriter cw) {
        // @JsonIgnoreProperties 忽略不知道欄位值，避免新增欄位造成錯誤
        cw.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonIgnoreProperties.class)).getDescriptor(), true).visit("ignoreUnknown", true);
    }

    @Override
    protected void setFieldAnnotation(MappingField f, FieldVisitor fv) {
        // @JsonProperty 設定輸出欄位名稱
        fv.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonProperty.class)).getDescriptor(), true).visit("value", f.getName());
        // @JsonAlias 設定來源欄位名稱
        AnnotationVisitor av = fv.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonAlias.class)).getDescriptor(), true).visitArray("value");
        av.visit(null, f.getSource());
        av.visitEnd();
    }
}
