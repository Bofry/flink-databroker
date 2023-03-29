package com.bofry.databroker.core.misc;

import com.bofry.databroker.core.component.ByteClassLoader;
import lombok.SneakyThrows;
import org.apache.flink.shaded.asm7.org.objectweb.asm.ClassWriter;
import org.apache.flink.shaded.asm7.org.objectweb.asm.MethodVisitor;
import org.apache.flink.shaded.asm7.org.objectweb.asm.Opcodes;

public class GeneratorClass {

    @SneakyThrows
    public static void main(String[] args) {
        //生成一个类只需要ClassWriter组件即可
        ClassWriter cw = new ClassWriter(0);
        //通过visit方法确定类的头部信息
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC,
                "com/asm3/Comparable", null, "java/lang/Object", null);
        //定义类的属性
        cw.visitField(Opcodes.ACC_PUBLIC,
                "LESS", "I", null, -1).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC,
                "EQUAL", "I", null, 0).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC,
                "GREATER", "I", null, 1).visitEnd();

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
        // clazz = 0x100,
        // 類似：Constructor = 0x100 + 20
        Object o = clazz.getDeclaredConstructor().newInstance();
        // clazz = 0x100,
        // 類似：Field = 0x100 + 50
        // 偏移位置
        clazz.getDeclaredField("LESS").set(o, 5);
        System.out.println(clazz.getDeclaredField("LESS").get(o));
        System.out.println(o);
    }

}
