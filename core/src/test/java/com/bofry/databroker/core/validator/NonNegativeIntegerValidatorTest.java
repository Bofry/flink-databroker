package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.ValidationException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidObjectException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class NonNegativeIntegerValidatorTest {

    private IValidator required;
    private Date date;
    private String fieldName;

    @Before
    public void setUp() {
        required = new NonNegativeIntegerValidator();
        date = new Date();
        fieldName = "NonNegativeIntegerValidatorTest";
    }

    @SneakyThrows
    @Test
    public void testNull() {
        required.validate(fieldName, null);
    }

    @SneakyThrows
    @Test(expected = InvalidObjectException.class)
    public void testEmptyString() {
        required.validate(fieldName, "");
    }

    @SneakyThrows
    @Test(expected = InvalidObjectException.class)
    public void testNonEmptyString() {
        required.validate(fieldName, " ");
    }

    @SneakyThrows
    @Test
    public void testPositiveInteger() {
        required.validate(fieldName, 1);
    }

    @SneakyThrows
    @Test
    public void testZeroInteger() {
        required.validate(fieldName, 0);
    }

    @SneakyThrows
    @Test(expected = ValidationException.class)
    public void testNegativeInteger() {
        required.validate(fieldName, -1);
    }

    @SneakyThrows
    @Test
    public void testPositiveLong() {
        required.validate(fieldName, 1L);
    }

    @SneakyThrows
    @Test
    public void testZeroLong() {
        required.validate(fieldName, 0L);
    }

    @SneakyThrows
    @Test(expected = ValidationException.class)
    public void testNegativeLong() {
        required.validate(fieldName, -1L);
    }

    @SneakyThrows
    @Test
    public void testPositiveBigDecimal() {
        required.validate(fieldName, new BigDecimal("1.0"));
    }

    @SneakyThrows
    @Test
    public void testZeroBigDecimal() {
        required.validate(fieldName, BigDecimal.ZERO);
    }

    @SneakyThrows
    @Test(expected = ValidationException.class)
    public void testNegativeBigDecimal() {
        required.validate(fieldName, new BigDecimal("-1.0"));
    }

    @SneakyThrows
    @Test(expected = InvalidObjectException.class)
    public void testSqlDate() {
        required.validate(fieldName, new java.sql.Date(date.getTime()));
    }

    @SneakyThrows
    @Test(expected = InvalidObjectException.class)
    public void testSqlTime() {
        required.validate(fieldName, new java.sql.Time(date.getTime()));
    }

    @SneakyThrows
    @Test(expected = InvalidObjectException.class)
    public void testSqlTimestamp() {
        required.validate(fieldName, new Timestamp(System.currentTimeMillis()));
    }
}
