package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.ValidationException;

public class RequiredValidator extends AbstractValidator {

    @Override
    public void validate(String fieldName, Object input) throws ValidationException {
        if (input == null) {
            throw new ValidationException(String.format("%s : Required verification failed.", fieldName));
        }
    }
}
