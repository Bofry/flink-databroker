package com.bofry.databroker.core.component;

public interface IElasticsearchMetadataBuilder {

    ElasticsearchMetadataItem createItem(String id, String index, String body);

}
