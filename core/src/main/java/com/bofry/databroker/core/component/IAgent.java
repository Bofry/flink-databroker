package com.bofry.databroker.core.component;

public interface IAgent {

    IConfiguration getConfiguration();

    void throwFailure(Exception e);

}
