package com.bofry.databroker.core.validator;

import com.bofry.databroker.core.component.SinkConfiguration;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public class CompositeValidatorTest {

    private IValidator required;
    private SinkConfiguration conf;

    @Before
    public void setUp() {
        conf = new SinkConfiguration();
        required = new CompositeValidator(this.conf);
    }

    @SneakyThrows
    @Test
    public void testNull() {
        required.validate(null);
    }

}
