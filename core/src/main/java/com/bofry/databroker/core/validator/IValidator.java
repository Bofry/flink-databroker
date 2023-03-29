package com.bofry.databroker.core.validator;

public interface IValidator {

    void validate(Object input) throws Exception;

    void validate(String fieldName, Object input) throws Exception;

}
