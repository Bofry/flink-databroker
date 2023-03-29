package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.ValidationException;

import java.io.InvalidObjectException;
import java.math.BigDecimal;

public class NonNegativeIntegerValidator extends AbstractValidator {
    @Override
    public void validate(String fieldName, Object input) throws InvalidObjectException, ValidationException {
        // skip if null
        if (input == null) {
            return;
        }

        ValidationException e = new ValidationException(String.format("%s : Non negative integer verification failed.", fieldName));

        if (input instanceof Integer) {
            if ((Integer) input < 0) {
                throw e;
            }
            return;
        } else if (input instanceof Long) {
            if ((Long) input < 0) {
                throw e;
            }
            return;
        } else if (input instanceof BigDecimal) {
            if (((BigDecimal) input).compareTo(BigDecimal.ZERO) < 0) {
                throw e;
            }
            return;
        }
        throw new InvalidObjectException(input.getClass().getName());

    }
}
