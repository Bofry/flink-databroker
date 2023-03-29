package com.bofry.databroker.core.component;

import java.io.Serializable;
import java.util.Map;

public interface IConfiguration extends Serializable {

    Map<String, Object> getConfig();

}
