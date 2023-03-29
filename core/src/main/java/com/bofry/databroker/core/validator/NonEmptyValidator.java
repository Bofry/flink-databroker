package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.ValidationException;

import java.io.InvalidObjectException;

public class NonEmptyValidator extends AbstractValidator {

    @Override
    public void validate(String fieldName, Object input) throws InvalidObjectException, ValidationException {
        if (input == null) {
            return;
        }

        if (input instanceof String) {
            if (((String) input).length() <= 0) {
                throw new ValidationException(String.format("%s : Non empty verification failed.", fieldName));
            }
            return;
        }
        throw new InvalidObjectException(input.getClass().getName());
    }
}
