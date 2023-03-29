package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class ElasticsearchMetadataItemFactory {

    private static final Map<String, IElasticsearchMetadataBuilder> BUILD_TABLE = new HashMap<>();

    static {
        BUILD_TABLE.put("index", new IndexMetadataBuilder());
        BUILD_TABLE.put("delete", new DeleteMetadataBuilder());
        BUILD_TABLE.put("create", new CreateMetadataBuilder());
        BUILD_TABLE.put("update", new UpdateMetadataBuilder());
    }

    @SneakyThrows
    public static ElasticsearchMetadataItem create(String action, String id, String index, String body) {
        IElasticsearchMetadataBuilder builder = BUILD_TABLE.get(action);
        return builder.createItem(id, index, body);
    }

}
