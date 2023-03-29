package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.ValidationException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class RequiredValidatorTest {

    private IValidator required;
    private Date date;
    private String fieldName;

    @Before
    public void setUp() {
        required = new RequiredValidator();
        date = new Date();
        fieldName = "RequiredValidatorTest";
    }

    @SneakyThrows
    @Test(expected = ValidationException.class)
    public void testNull() {
        required.validate(fieldName, null);
    }

    @SneakyThrows
    @Test
    public void testEmptyString() {
        required.validate(fieldName, "");
    }

    @SneakyThrows
    @Test
    public void testNonEmptyString() {
        required.validate(fieldName, " ");
    }

    @SneakyThrows
    @Test
    public void testInteger() {
        required.validate(fieldName, 1);
    }

    @SneakyThrows
    @Test
    public void testLong() {
        required.validate(fieldName, 1L);
    }

    @SneakyThrows
    @Test
    public void testBigDecimal() {
        required.validate(fieldName, new BigDecimal("1.0"));
    }

    @SneakyThrows
    @Test
    public void testSqlDate() {
        required.validate(fieldName, new java.sql.Date(date.getTime()));
    }

    @SneakyThrows
    @Test
    public void testSqlTime() {
        required.validate(fieldName, new java.sql.Time(date.getTime()));
    }

    @SneakyThrows
    @Test
    public void testSqlTimestamp() {
        required.validate(fieldName, new Timestamp(System.currentTimeMillis()));
    }

}
