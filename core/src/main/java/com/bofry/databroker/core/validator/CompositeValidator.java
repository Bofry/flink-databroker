package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.MappingField;
import com.bofry.databroker.core.component.SinkConfiguration;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeValidator extends AbstractValidator {

    private static final Map<String, IValidator> VALIDATOR = new HashMap<>();

    static {
        VALIDATOR.put("required",             new RequiredValidator());
        VALIDATOR.put("non_empty",            new NonEmptyValidator());
        VALIDATOR.put("non_zero",             new NonZeroValidator());
        VALIDATOR.put("non_negative_integer", new NonNegativeIntegerValidator());
    }

    private final List<MappingField> mapping;
    private final Map<String, List<String>> fieldTag = new HashMap<>();

    public CompositeValidator(SinkConfiguration conf) {
        this.mapping = conf.getMapping();

        initTag();
    }

    private void initTag() {
        for (MappingField m : this.mapping) {
            if (m.getTag() != null && m.getTag().size() > 0) {
                this.fieldTag.put(m.getName(), m.getTag());
            }
        }
    }

    @SneakyThrows
    public void validate(Object input) {
        if (input == null) {
            return;
        }
        for (Field field : input.getClass().getDeclaredFields()) {
            String fieldName = field.getName();

            List<String> tags = this.fieldTag.get(fieldName);
            if (tags == null) {
                continue;
            }
            for (String tag : tags) {
                IValidator v = VALIDATOR.get(tag);
                if (v == null) {
                    // TODO find suitable exception
                    throw new NoSuchMethodException();
                }
                v.validate(fieldName, field.get(input));
            }
        }
    }

}
