package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.type.Date;
import com.bofry.databroker.core.script.type.Time;
import com.bofry.databroker.core.script.type.Timestamp;
import org.apache.flink.shaded.asm7.org.objectweb.asm.Type;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class MysqlDescriptorTest {

    private final IObjectDescriptor baseDescriptor  = new DescriptorDecorator(null);
    private final IObjectDescriptor MysqlDescriptor = new DescriptorDecorator(new MysqlDescriptor());

    @Test
    public void testBaseBooleanDescriptor() {
        Assert.assertEquals(baseDescriptor.get("boolean"), Type.getObjectType(Type.getInternalName(Boolean.class)).getDescriptor());
    }

    @Test
    public void testBaseStringDescriptor() {
        Assert.assertEquals(baseDescriptor.get("string"), Type.getObjectType(Type.getInternalName(String.class)).getDescriptor());
    }

    @Test
    public void testBaseIntegerDescriptor() {
        Assert.assertEquals(baseDescriptor.get("integer"), Type.getObjectType(Type.getInternalName(Integer.class)).getDescriptor());
    }

    @Test
    public void testBaseLongDescriptor() {
        Assert.assertEquals(baseDescriptor.get("long"), Type.getObjectType(Type.getInternalName(Long.class)).getDescriptor());
    }

    @Test
    public void testBaseDecimalDescriptor() {
        Assert.assertEquals(baseDescriptor.get("decimal"), Type.getObjectType(Type.getInternalName(BigDecimal.class)).getDescriptor());
    }

    @Test
    public void testBaseDateDescriptor() {
        Assert.assertEquals(baseDescriptor.get("date"), Type.getObjectType(Type.getInternalName(Date.class)).getDescriptor());
    }

    @Test
    public void testBaseTimeDescriptor() {
        Assert.assertEquals(baseDescriptor.get("time"), Type.getObjectType(Type.getInternalName(Time.class)).getDescriptor());
    }

    @Test
    public void testBaseTimestampDescriptor() {
        Assert.assertEquals(baseDescriptor.get("timestamp"), Type.getObjectType(Type.getInternalName(Timestamp.class)).getDescriptor());
    }

}
