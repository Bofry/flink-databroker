package com.bofry.databroker.core.statestore;

public interface IPayloadFormatter {
    String marshal(Object entity);
}
