package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.type.Date;
import com.bofry.databroker.core.script.type.Time;
import com.bofry.databroker.core.script.type.Timestamp;
import org.apache.flink.shaded.asm7.org.objectweb.asm.Type;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class PostgresqlDescriptorTest {

    private final IObjectDescriptor baseDescriptor  = new DescriptorDecorator(null);
    private final IObjectDescriptor PostgresqlDescriptor = new DescriptorDecorator(new PostgresqlDescriptor());

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

    @Test
    public void testPostgresqlJsonDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("json"), Type.getObjectType(Type.getInternalName(RawContent.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlStringArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("string[]"), "[" + Type.getObjectType(Type.getInternalName(String.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlShortArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("short[]"), "[" + Type.getObjectType(Type.getInternalName(Short.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlIntegerArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("integer[]"), "[" + Type.getObjectType(Type.getInternalName(Integer.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlLongArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("long[]"), "[" + Type.getObjectType(Type.getInternalName(Long.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlFloatArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("float[]"), "[" + Type.getObjectType(Type.getInternalName(Float.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlDoubleArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("double[]"), "[" + Type.getObjectType(Type.getInternalName(Double.class)).getDescriptor());
    }

    @Test
    public void testPostgresqlBooleanArrayDescriptor() {
        Assert.assertEquals(PostgresqlDescriptor.get("boolean[]"), "[" + Type.getObjectType(Type.getInternalName(Boolean.class)).getDescriptor());
    }


}
