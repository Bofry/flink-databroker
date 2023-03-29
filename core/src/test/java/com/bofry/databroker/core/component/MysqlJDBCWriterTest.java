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

public class MysqlJDBCWriterTest {

    private static Properties properties;
    private static MysqlTestModel model;
    private static Class clazz;
    private static OutputStreamContext ctx;
    private static DummyAgent agent;

    @BeforeClass
    public static void setUp() {
        properties = new Properties();
        // TODO 改取環境變數
        properties.put("host", "jdbc:mysql://127.0.0.1:3306/testdb?allowMultiQueries=true");
        properties.put("user", "root");
        properties.put("password", "root");

        clazz = MysqlTestModel.class;
        java.util.Date date = new java.util.Date();

        model = MysqlTestModel.builder()
                .b(true)
                .s("Hello")
                .i(9999)
                .l(220000000L)
                .bd(new BigDecimal("3345.678"))
                .d(new Date(date.getTime()))
                .t(new Time(date.getTime()))
                .ts(new Timestamp(System.currentTimeMillis()))
                .e(Gender.M.toString())
                .build();

        ctx = new OutputStreamContext(new EntityValue(model), clazz, InputStreamContext.builder().build());

        agent = new DummyAgent();

        String sql = "DROP TABLE IF EXISTS `test_bigint`;\n" +
                "     CREATE TABLE `test_bigint` (\n" +
                "     `v` bigint DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_bit`;\n" +
                "     CREATE TABLE `test_bit` (\n" +
                "     `v` bit(1) DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_char`;\n" +
                "     CREATE TABLE `test_char` (\n" +
                "     `v` char(10) COLLATE utf8mb4_general_ci DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_date`;\n" +
                "     CREATE TABLE `test_date` (\n" +
                "     `v` date DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_datetime`;\n" +
                "     CREATE TABLE `test_datetime` (\n" +
                "     `v` datetime DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_decimal`;\n" +
                "     CREATE TABLE `test_decimal` (\n" +
                "     `v` decimal(15,5) DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_enum`;\n" +
                "     CREATE TABLE `test_enum` (\n" +
                "     `v` enum('M','F') COLLATE utf8mb4_general_ci DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_int`;\n" +
                "     CREATE TABLE `test_int` (\n" +
                "     `v` int DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_longtext`;\n" +
                "     CREATE TABLE `test_longtext` (\n" +
                "     `v` longtext COLLATE utf8mb4_general_ci\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_mediumint`;\n" +
                "     CREATE TABLE `test_mediumint` (\n" +
                "     `v` mediumint DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_mediumtext`;\n" +
                "     CREATE TABLE `test_mediumtext` (\n" +
                "     `v` mediumtext COLLATE utf8mb4_general_ci\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_smallint`;\n" +
                "     CREATE TABLE `test_smallint` (\n" +
                "     `v` smallint DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_text`;\n" +
                "     CREATE TABLE `test_text` (\n" +
                "     `v` text COLLATE utf8mb4_general_ci\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_time`;\n" +
                "     CREATE TABLE `test_time` (\n" +
                "     `v` time DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_timestamp`;\n" +
                "     CREATE TABLE `test_timestamp` (\n" +
                "     `v` timestamp NULL DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_tinyint`;\n" +
                "     CREATE TABLE `test_tinyint` (\n" +
                "     `v` tinyint DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_tinytext`;\n" +
                "     CREATE TABLE `test_tinytext` (\n" +
                "     `v` tinytext COLLATE utf8mb4_general_ci\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;\n" +
                "\n" +
                "     DROP TABLE IF EXISTS `test_varchar`;\n" +
                "     CREATE TABLE `test_varchar` (\n" +
                "     `v` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL\n" +
                "     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        properties.put("sql", sql);
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(null);

    }

    @AfterClass
    public static void tearDown() {

        String sql = "DROP TABLE IF EXISTS `test_bigint`;\n" +
                "DROP TABLE IF EXISTS `test_bit`;\n" +
                "DROP TABLE IF EXISTS `test_char`;\n" +
                "DROP TABLE IF EXISTS `test_date`;\n" +
                "DROP TABLE IF EXISTS `test_datetime`;\n" +
                "DROP TABLE IF EXISTS `test_decimal`;\n" +
                "DROP TABLE IF EXISTS `test_enum`;\n" +
                "DROP TABLE IF EXISTS `test_int`;\n" +
                "DROP TABLE IF EXISTS `test_longtext`;\n" +
                "DROP TABLE IF EXISTS `test_mediumint`;\n" +
                "DROP TABLE IF EXISTS `test_mediumtext`;\n" +
                "DROP TABLE IF EXISTS `test_smallint`;\n" +
                "DROP TABLE IF EXISTS `test_text`;\n" +
                "DROP TABLE IF EXISTS `test_time`;\n" +
                "DROP TABLE IF EXISTS `test_timestamp`;\n" +
                "DROP TABLE IF EXISTS `test_tinyint`;\n" +
                "DROP TABLE IF EXISTS `test_tinytext`;\n" +
                "DROP TABLE IF EXISTS `test_varchar`;";

        properties.put("sql", sql);
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(null);

    }

    @Test
    public void testBit() {
        properties.put("sql", "INSERT INTO `testdb`.`test_bit` VALUES (%{b});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTinyint() {
        properties.put("sql", "INSERT INTO `testdb`.`test_tinyint` VALUES (%{b});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testSmallint() {
        properties.put("sql", "INSERT INTO `testdb`.`test_smallint` VALUES (%{i});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testMediumint() {
        properties.put("sql", "INSERT INTO `testdb`.`test_mediumint` VALUES (%{i});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testInt() {
        properties.put("sql", "INSERT INTO `testdb`.`test_int` VALUES (%{i});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testBigint() {
        properties.put("sql", "INSERT INTO `testdb`.`test_bigint` VALUES (%{l});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testDecimal() {
        properties.put("sql", "INSERT INTO `testdb`.`test_decimal` VALUES (%{bd});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testDate() {
        properties.put("sql", "INSERT INTO `testdb`.`test_date` VALUES (%{d});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testDatetime() {
        properties.put("sql", "INSERT INTO `testdb`.`test_datetime` VALUES (%{ts});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTimestamp() {
        properties.put("sql", "INSERT INTO `testdb`.`test_timestamp` VALUES (%{ts});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTime() {
        properties.put("sql", "INSERT INTO `testdb`.`test_time` VALUES (%{t});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testChar() {
        properties.put("sql", "INSERT INTO `testdb`.`test_char` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testVarchar() {
        properties.put("sql", "INSERT INTO `testdb`.`test_varchar` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testTinytext() {
        properties.put("sql", "INSERT INTO `testdb`.`test_tinytext` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testText() {
        properties.put("sql", "INSERT INTO `testdb`.`test_text` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testMediumtext() {
        properties.put("sql", "INSERT INTO `testdb`.`test_mediumtext` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testLongtext() {
        properties.put("sql", "INSERT INTO `testdb`.`test_longtext` VALUES (%{s});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Test
    public void testEnum() {
        properties.put("sql", "INSERT INTO `testdb`.`test_enum` VALUES (%{e});");
        JDBCWriter writer = new JDBCWriter(properties, agent);
        writer.execute(ctx);
    }

    @Data
    @Builder
    private static class MysqlTestModel implements Serializable {

        Boolean b;
        String s;
        Integer i;
        Long l;
        BigDecimal bd;
        Date d;
        Time t;
        Timestamp ts;
        String e;

    }

    private enum Gender {
        M, F
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
