package com.bofry.databroker.core.misc;

import com.bofry.databroker.core.component.ByteClassLoader;
import com.bofry.databroker.core.component.JacksonUtils;
import com.bofry.databroker.core.component.RawContent;
import com.bofry.databroker.core.serdes.RowContentDeserializer;
import com.bofry.databroker.core.serdes.RowContentSerializer;
import lombok.SneakyThrows;
import org.apache.flink.shaded.asm7.org.objectweb.asm.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.reflect.Field;
import java.util.Arrays;

public class GeneratorJsonRowClass {

    @SneakyThrows
    public static void main(String[] args) {
        //生成一个类只需要ClassWriter组件即可
        ClassWriter cw = new ClassWriter(0);
        //通过visit方法确定类的头部信息
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC,
                "com/asm3/Comparable", null, Type.getInternalName(Object.class), null);
        cw.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonIgnoreProperties.class)).getDescriptor(), true).visit("ignoreUnknown", true);
        //定义类的属性
        {
            FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC, "symbol", Type.getObjectType(Type.getInternalName(RawContent.class)).getDescriptor(), null, null);
            fv.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonDeserialize.class)).getDescriptor(), true)
                    .visit("using", Type.getObjectType(Type.getInternalName(RowContentDeserializer.class)));
            fv.visitAnnotation(Type.getObjectType(Type.getInternalName(JsonSerialize.class)).getDescriptor(), true)
                    .visit("using", Type.getObjectType(Type.getInternalName(RowContentSerializer.class)));
            fv.visitEnd();
        }

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        // 將資料放到記憶體位置內
        byte[] data = cw.toByteArray();
        // 載入主鍵(.class, assembly) 到可執行區
        ClassLoader cl = new ByteClassLoader(data);
        // 要把這個 Class 載入 (類似 Class.forName("com.bla.TestActivity");)
        // 我現在要 Reflect (反射)
        Class clazz = cl.loadClass("com.asm3.Comparable");

        System.out.println(Arrays.toString(clazz.getAnnotations()));

        for (Field f : clazz.getDeclaredFields()) {
            System.out.println(f + " " + Arrays.toString(f.getAnnotations()));
        }

        Object o = clazz.getDeclaredConstructor().newInstance();

        o = JacksonUtils.OBJECT_MAPPER.readValue("{\n" +
                "   \"symbol\":{\"key\":\"hello\"}\n" +
                "}", clazz);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(o));
    }

}
