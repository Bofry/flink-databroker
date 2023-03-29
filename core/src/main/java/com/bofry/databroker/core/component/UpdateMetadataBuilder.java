package com.bofry.databroker.core.component;

public class UpdateMetadataBuilder implements IElasticsearchMetadataBuilder {

    // this is update template
    private static final String META_TEMPLATE = "{ \"update\" : { \"_id\" : \"%s\", \"_type\" : \"_doc\", \"_index\" : \"%s\"} }";

    @Override
    public ElasticsearchMetadataItem createItem(String id, String index, String body) {
        if (id == null || index == null || body == null) {
            throw new IllegalArgumentException();
        }

        return ElasticsearchMetadataItem.builder()
                .metadata(String.format(META_TEMPLATE, id, index))
                .body(body)
                .build();
    }

}