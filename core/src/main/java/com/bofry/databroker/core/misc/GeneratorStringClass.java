package com.bofry.databroker.core.misc;

import com.bofry.databroker.core.component.ByteClassLoader;
import com.bofry.databroker.core.component.JacksonUtils;
import lombok.SneakyThrows;
import org.apache.flink.shaded.asm7.org.objectweb.asm.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.Arrays;

public class GeneratorStringClass {

    @SneakyThrows
    public static void main(String[] args) {
        //生成一个类只需要ClassWriter组件即可
        ClassWriter cw = new ClassWriter(0);
        //通过visit方法确定类的头部信息
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC,
                "com/asm3/Comparable", null, "java/lang/Object", null);
        cw.visitAnnotation("Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonIgnoreProperties;", true).visit("ignoreUnknown", true);
//        cw.visitAnnotation("Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonInclude;", true).visit("value", "NON_NULL");
//        AnnotationVisitor va = cw.visitAnnotation("Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonInclude;", true);
//        va.visitEnum("value", "/Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonInclude/Include", "NON_NULL");
//        va.visitEnd();

//        cw.visitAnnotation("Lcom/fasterxml/jackson/annotation/JsonIgnoreProperties;", true).visit("ignoreUnknown", true);

        //定义类的属性
//        cw.visitField(Opcodes.ACC_PUBLIC, "LESS", "I", null, new Integer(9)).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC, "vendor", "Ljava/lang/String;", null, null).visitEnd();

        FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC, "player", "Ljava/lang/String;", null, null);

        fv.visitAnnotation("Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonProperty;", true).visit("value", "play");
//                fv.visitAnnotation("Lcom/fasterxml/jackson/annotation/JsonProperty;", true).visit("value", "play");

        AnnotationVisitor av = fv.visitAnnotation("Lorg/apache/flink/shaded/jackson2/com/fasterxml/jackson/annotation/JsonAlias;", true).visitArray("value");
//        AnnotationVisitor av = fv.visitAnnotation("Lcom/fasterxml/jackson/annotation/JsonAlias;", true).visitArray("value");
        av.visit(null, "player");
        av.visitEnd();
        fv.visitEnd();

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
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

//        Class clazz = cl.loadClass("com.bofry.archiver.component.SourceConfiguration");

        /**
         // clazz = 0x100,
         // 類似：Constructor = 0x100 + 20
         Object o = clazz.getDeclaredConstructor().newInstance();
         // clazz = 0x100,
         // 類似：Field = 0x100 + 50
         // 偏移位置
         clazz.getDeclaredField("LESS").set(o, "5");
         System.out.println(clazz.getDeclaredField("LESS").get(o));
         System.out.println(o);
         **/

        System.out.println(Arrays.toString(clazz.getAnnotations()));

        for (Field f : clazz.getDeclaredFields()) {
            System.out.println(f + " " + Arrays.toString(f.getAnnotations()));
        }

        Object o = clazz.getDeclaredConstructor().newInstance();
        // clazz = 0x100,
        // 類似：Field = 0x100 + 50
        // 偏移位置
        clazz.getDeclaredField("vendor").set(o, "1234");

        o = JacksonUtils.OBJECT_MAPPER.readValue("{\"player\":\"J.J.\"}", clazz);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(o));
    }

}
