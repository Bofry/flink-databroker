package com.bofry.databroker.core.component;

import com.bofry.databroker.core.script.type.Date;
import com.bofry.databroker.core.script.type.Time;
import com.bofry.databroker.core.script.type.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.UUID;

public class PostgresqlJDBCWriterTest {

    private static Properties properties;
    private static PostgresqlTestModel model;
    private static Class clazz;
    private static OutputStreamContext ctx;
    private static DummyAgent agent;

    /**
     * 沒有測試 bytea, interval, xml
     */

    @BeforeClass
    public static void setUp() {
        properties = new Properties();
        // TODO 改取環境變數
        properties.put("host", "jdbc:postgresql://127.0.0.1:5432/testdb");
        properties.put("user", "postgres");
        properties.put("password", "example");

        clazz = PostgresqlTestModel.class;
        java.util.Date date = new java.util.Date();

        model = PostgresqlTestModel.builder()
                .ba("J".getBytes())
                .b(true)
                .s("J")
                .i(9999)
                .l(220000000L)
                .f(330000000.123F)
                .bd(new BigDecimal("3345.678"))
                .d(new Date(date.getTime()))
                .t(new Time(date.getTime()))
                .ts(new Timestamp(System.currentTimeMillis()))
                .j("{\"player\":\"JJ\"}")
                .uuid(UUID.randomUUID())

                ._string(new String[]{"AA", "BB"})
                ._short(new Short[]{123, 456})
                ._integer(new Integer[]{2222, 1111})
                ._long(new Long[]{44444L, 3333L})
                ._float(new Float[]{666.66F, 555.55F})
                ._double(new Double[]{999.99D, 888.88D})
                ._boolean(new Boolean[]{Boolean.TRUE, Boolean.FALSE})
                .build();

        ctx = new OutputStreamContext(new EntityValue(model), clazz, InputStreamContext.builder().build());

        agent = new DummyAgent();

        String sql = "DROP TABLE IF EXISTS test_bytea;\n" +
                "CREATE TABLE test_bytea (\n" +
                "    v bytea\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_bool;\n" +
                "CREATE TABLE test_bool (\n" +
                "    v bool\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_char;\n" +
                "CREATE TABLE test_char (\n" +
                "    v char\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_date;\n" +
                "CREATE TABLE test_date (\n" +
                "    v date\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_float4;\n" +
                "CREATE TABLE test_float4 (\n" +
                "    v float4\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_float8;\n" +
                "CREATE TABLE test_float8 (\n" +
                "    v float8\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_int2;\n" +
                "CREATE TABLE test_int2 (\n" +
                "    v int2\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_int4;\n" +
                "CREATE TABLE test_int4 (\n" +
                "    v int4\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_int8;\n" +
                "CREATE TABLE test_int8 (\n" +
                "    v int8\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_json;\n" +
                "CREATE TABLE test_json (\n" +
                "    v json\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_jsonb;\n" +
                "CREATE TABLE test_jsonb (\n" +
                "    v jsonb\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_numeric;\n" +
                "CREATE TABLE test_numeric (\n" +
                "    v numeric\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_text;\n" +
                "CREATE TABLE test_text (\n" +
                "    v text\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_time;\n" +
                "CREATE TABLE test_time (\n" +
                "    v time\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_timestamp;\n" +
                "CREATE TABLE test_timestamp (\n" +
                "    v timestamp\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_timestamptz;\n" +
                "CREATE TABLE test_timestamptz (\n" +
                "    v timestamptz\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_timetz;\n" +
                "CREATE TABLE test_timetz (\n" +
                "    v timetz\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_uuid;\n" +
                "CREATE TABLE test_uuid (\n" +
                "    v uuid\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_varchar;\n" +
                "CREATE TABLE test_varchar (\n" +
                "    v varchar\n" +
                ");\n" +
                "DROP TABLE IF EXISTS test_array;\n" +
                "CREATE TABLE test_array (\n" +
                "    _string _varchar,\n" +
                "    _short _int2,\n" +
                "    _integer _int4,\n" +
                "    _long _int8,\n" +
                "    _float _float4,\n" +
                "    _double _float8,\n" +
                "    _boolean _bool\n" +
                ");";

        properties.put("sql", sql);
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(null);

    }

    @AfterClass
    public static void tearDown() {

        String sql = "DROP TABLE IF EXISTS test_bytea;\n" +
                "DROP TABLE IF EXISTS test_bool;\n" +
                "DROP TABLE IF EXISTS test_char;\n" +
                "DROP TABLE IF EXISTS test_date;\n" +
                "DROP TABLE IF EXISTS test_float4;\n" +
                "DROP TABLE IF EXISTS test_float8;\n" +
                "DROP TABLE IF EXISTS test_int2;\n" +
                "DROP TABLE IF EXISTS test_int4;\n" +
                "DROP TABLE IF EXISTS test_int8;\n" +
                "DROP TABLE IF EXISTS test_json;\n" +
                "DROP TABLE IF EXISTS test_jsonb;\n" +
                "DROP TABLE IF EXISTS test_numeric;\n" +
                "DROP TABLE IF EXISTS test_text;\n" +
                "DROP TABLE IF EXISTS test_time;\n" +
                "DROP TABLE IF EXISTS test_timestamp;\n" +
                "DROP TABLE IF EXISTS test_timestamptz;\n" +
                "DROP TABLE IF EXISTS test_timetz;\n" +
                "DROP TABLE IF EXISTS test_uuid;\n" +
                "DROP TABLE IF EXISTS test_varchar;\n" +
                "DROP TABLE IF EXISTS test_array;";

        properties.put("sql", sql);
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(null);

    }

    @Test
    public void testBytea() {
        properties.put("sql", "INSERT INTO test_bytea VALUES (%{ba});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testBool() {
        properties.put("sql", "INSERT INTO test_bool VALUES (%{b});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testChar() {
        properties.put("sql", "INSERT INTO test_char VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testDate() {
        properties.put("sql", "INSERT INTO test_date VALUES (%{d});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testFloat4() {
        properties.put("sql", "INSERT INTO test_float4 VALUES (%{f});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testFloat8() {
        properties.put("sql", "INSERT INTO test_float8 VALUES (%{f});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testInt2() {
        properties.put("sql", "INSERT INTO test_int2 VALUES (%{i});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testInt4() {
        properties.put("sql", "INSERT INTO test_int4 VALUES (%{i});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testInt8() {
        properties.put("sql", "INSERT INTO test_int8 VALUES (%{l});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testJson() {
        properties.put("sql", "INSERT INTO test_json VALUES (%{j}::json);");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testJsonb() {
        properties.put("sql", "INSERT INTO test_jsonb VALUES (%{j}::json);");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testNumeric() {
        properties.put("sql", "INSERT INTO test_numeric VALUES (%{bd});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testText() {
        properties.put("sql", "INSERT INTO test_text VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTime() {
        properties.put("sql", "INSERT INTO test_time VALUES (%{t});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTimestamp() {
        properties.put("sql", "INSERT INTO test_timestamp VALUES (%{ts});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTimestamptz() {
        properties.put("sql", "INSERT INTO test_timestamptz VALUES (%{ts});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTimetz() {
        properties.put("sql", "INSERT INTO test_timetz VALUES (%{t});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testUuid() {
        properties.put("sql", "INSERT INTO test_uuid VALUES (%{uuid});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testVarchar() {
        properties.put("sql", "INSERT INTO test_varchar VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testArray() {
        properties.put("sql", "INSERT INTO test_array (_string, _short, _integer, _long, _float, _double, _boolean) VALUES (%{_string}, %{_short}, %{_integer}, %{_long}, %{_float}, %{_double}, %{_boolean})");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Data
    @Builder
    private static class PostgresqlTestModel implements Serializable {

        byte[] ba;
        Boolean b;
        String s;
        Integer i;
        Long l;
        Float f;
        BigDecimal bd;
        Date d;
        Time t;
        Timestamp ts;
        String j;
        UUID uuid;

        String[] _string;
        Short[] _short;
        Integer[] _integer;
        Long[] _long;
        Float[] _float;
        Double[] _double;
        Boolean[] _boolean;

    }

    static class DummyAgent implements IAgent{

        @Override
        public IConfiguration getConfiguration() {
            return null;
        }

        @Override
        public void throwFailure(Exception e) {
            FailureManager failure = new FailureManager();
            failure.process(e);
        }
    }

}
