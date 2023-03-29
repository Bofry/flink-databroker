package com.bofry.databroker.core.component;

public class ByteClassLoader extends ClassLoader {

    private final byte[] byteCode;

    public ByteClassLoader(byte[] byteCode) {
        this.byteCode = byteCode;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (this.byteCode != null) {
            return defineClass(name, this.byteCode, 0, this.byteCode.length);
        }
        return super.findClass(name);
    }

}
