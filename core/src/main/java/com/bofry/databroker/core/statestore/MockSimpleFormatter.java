package com.bofry.databroker.core.statestore;

public class MockSimpleFormatter implements IPayloadFormatter {
    public static final MockSimpleFormatter INSTANCE = new MockSimpleFormatter();

    private MockSimpleFormatter() {
        super();
    }

    @Override
    public String marshal(Object entity) {
        return entity.toString() + "\n";
    }
}
