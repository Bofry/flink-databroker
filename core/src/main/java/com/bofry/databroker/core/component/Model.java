package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.TimestampPrecision;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.shaded.asm7.org.objectweb.asm.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public abstract class Model {

    private final Class clazz;
    private final DefaultValueProvider defaultValueProvider;

    protected Model(String className, Map<String, MappingField> modelMapping, Map<String, MappingField> srcMapping, IObjectDescriptor descriptor) throws Exception {
        if (StringUtils.isEmpty(className) ||
                modelMapping == null ||
                modelMapping.isEmpty() ||
                descriptor == null) {
            throw new IllegalArgumentException();
        }
        // plugin 注入當前的 classLoader。
        injectJars();
        ClassLoader cl = new ByteClassLoader(this.defineModelClass(className, modelMapping, srcMapping, descriptor));
        this.clazz = cl.loadClass(className);
        this.defaultValueProvider = new DefaultValueProvider(modelMapping);
    }

    protected String getFieldName(String name) {
        return name;
    }

    protected void setClassAnnotation(ClassWriter cw) {
    }

    protected void setFieldAnnotation(MappingField f, FieldVisitor fv) {
    }

    private void setFieldAnnotationCompleted(FieldVisitor fv, MappingField modelField, MappingField srcField) {
        if (modelField.getType().equals("timestamp")) {
            if (srcField != null) {
                AnnotationVisitor av = fv.visitAnnotation(Type.getObjectType(Type.getInternalName(TimestampPrecision.class)).getDescriptor(), true);
                // TODO 確認如果這兩個值是 null 的處理方式。
                av.visit("source", srcField.getPrecision());
                av.visit("destination", modelField.getPrecision());
                av.visitEnd();
            }
        }
    }

    protected final byte[] defineModelClass(String className, Map<String, MappingField> modelMapping, Map<String, MappingField> srcMapping, IObjectDescriptor descriptor) {
        String classFullName = className.replaceAll("\\.", "/");
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, classFullName, null, Type.getInternalName(Object.class), null);
        setClassAnnotation(cw);
        // 設定欄位
        modelMapping.forEach((k, v) -> {
            String fieldName = getFieldName(k);
            String fieldType = descriptor.get(v.getType());
            MappingField srcField;
            srcField = srcMapping.get(v.getSource());

            FieldVisitor fv = cw.visitField(
                    Opcodes.ACC_PUBLIC,
                    fieldName,
                    fieldType,
                    null,
                    null);

            setFieldAnnotation(v, fv);
            setFieldAnnotationCompleted(fv, v, srcField);
            fv.visitEnd();

            String fieldUpperName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            // get set 方法名
            String getMethodName = "get" + fieldUpperName;
            String setMethodName = "set" + fieldUpperName;

            // 建立 Get 方法
            MethodVisitor getMV = cw.visitMethod(Opcodes.ACC_PUBLIC, getMethodName, "()" + fieldType, null, null);
            getMV.visitCode();
            getMV.visitVarInsn(Opcodes.ALOAD, 0);
            getMV.visitFieldInsn(Opcodes.GETFIELD, classFullName, fieldName, fieldType);
            getMV.visitInsn(Opcodes.ARETURN);
            getMV.visitMaxs(1, 1);
            if (getMV != null) {
                getMV.visitEnd();
            }

            // 建立 Set 方法
            MethodVisitor setMV = cw.visitMethod(Opcodes.ACC_PUBLIC, setMethodName, "(" + fieldType + ")V", null, null);
            setMV.visitCode();
            setMV.visitVarInsn(Opcodes.ALOAD, 0);
            setMV.visitVarInsn(Opcodes.ALOAD, 1);
            setMV.visitFieldInsn(Opcodes.PUTFIELD, classFullName, fieldName, fieldType);
            setMV.visitInsn(Opcodes.RETURN);
            setMV.visitMaxs(2, 2);
            if (setMV != null) {
                setMV.visitEnd();
            }

        });

        // 建構子
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();

        return cw.toByteArray();
    }

    // TODO 來源不是只有一種，不能用單純String
    // source -> fill 錯誤的
    // source -> 判斷type 中介 to Map -> fill(map) (這正確的)
    @SneakyThrows
    public Object fillFromJson(String value) {
        Object model = JacksonUtils.toObject(value, this.clazz);
        defaultValueProvider.fill(model);
        return model;
    }

    private void injectJars() {
        try {
            // 取得 class 實際的 library 的實際位置
            File lib = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            URL[] jars = new URL[]{lib.toURI().toURL()};
            this.injectJars(jars);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void injectJars(URL[] jars) throws Exception {
        /**
         * 使用 hack 方式去注入 檔案位置。
         */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null && cl.getParent() != null) {
            cl = cl.getParent();
            if (cl instanceof URLClassLoader) {
                URLClassLoader loader = ((URLClassLoader) cl);
                Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addURL.setAccessible(true);
                for (int i = 0; i < jars.length; i++) {
                    addURL.invoke(loader, jars[i]);
                }
            }
        }
    }

    public Class getModelClass() {
        return clazz;
    }

}
