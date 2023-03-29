package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticsearchRequestModel {

    private String endpoint;
    private String jsonEntity;

}
