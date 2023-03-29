package com.bofry.databroker.core.component;

import java.io.Serializable;

public interface IObjectDescriptor extends Serializable {

    String get(String key);

}
