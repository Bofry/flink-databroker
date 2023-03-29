package com.bofry.databroker.core.component;

import java.io.Serializable;

public interface INameMatcher extends Serializable {

    void init();

    boolean match(String srcRoute);

}
