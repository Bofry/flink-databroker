package com.bofry.databroker.core.component;

import org.junit.Assert;
import org.junit.Test;

public class ElasticsearchMetadataBuilderTest {

    @Test
    public void IndexMetadataBuilderTest() {
        String id = "1";
        String index = "index";
        String body = "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }";

        IElasticsearchMetadataBuilder builder = new IndexMetadataBuilder();
        ElasticsearchMetadataItem item = builder.createItem(id, index, body);

        ElasticsearchMetadataItem data = ElasticsearchMetadataItem.builder()
                .metadata("{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }")
                .build();

        Assert.assertEquals(item.getMetadata(), data.getMetadata());
        Assert.assertEquals(item.getBody(), data.getBody());
    }

    @Test
    public void CreateMetadataBuilderTest() {
        String id = "1";
        String index = "index";
        String body = "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }";

        IElasticsearchMetadataBuilder builder = new CreateMetadataBuilder();
        ElasticsearchMetadataItem item = builder.createItem(id, index, body);

        ElasticsearchMetadataItem data = ElasticsearchMetadataItem.builder()
                .metadata("{ \"create\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }")
                .build();

        Assert.assertEquals(item.getMetadata(), data.getMetadata());
        Assert.assertEquals(item.getBody(), data.getBody());
    }

    @Test
    public void DeleteMetadataBuilderTest() {
        String id = "1";
        String index = "index";
        String body = "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }";

        IElasticsearchMetadataBuilder builder = new DeleteMetadataBuilder();
        ElasticsearchMetadataItem item = builder.createItem(id, index, body);

        ElasticsearchMetadataItem data = ElasticsearchMetadataItem.builder()
                .metadata("{ \"delete\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }")
                .build();

        Assert.assertEquals(item.getMetadata(), data.getMetadata());
        Assert.assertEquals(item.getBody(), data.getBody());
    }

    @Test
    public void UpdateMetadataBuilderTest() {
        String id = "1";
        String index = "index";
        String body = "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }";

        IElasticsearchMetadataBuilder builder = new UpdateMetadataBuilder();
        ElasticsearchMetadataItem item = builder.createItem(id, index, body);

        ElasticsearchMetadataItem data = ElasticsearchMetadataItem.builder()
                .metadata("{ \"update\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }")
                .build();

        Assert.assertEquals(item.getMetadata(), data.getMetadata());
        Assert.assertEquals(item.getBody(), data.getBody());
    }

}
