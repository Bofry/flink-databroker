package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public final class FailureContext {

    private FailureActionEnum action;
    private Map<String, Object> content;

}

