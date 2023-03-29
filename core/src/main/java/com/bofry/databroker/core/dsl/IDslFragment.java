package com.bofry.databroker.core.dsl;

import java.util.Map;

interface IDslFragment {
    String apply(Map<String, Object> values);
}
