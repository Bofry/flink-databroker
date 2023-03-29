package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RawContent implements Serializable {
    private static final long serialVersionUID = -4828832486954109395L;

    private String name;
    private String data;
}

