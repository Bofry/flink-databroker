package com.bofry.databroker.core.component;

import java.io.Serializable;

public interface IFailureHandler extends Serializable {

    boolean process(Exception e);

}