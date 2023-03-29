package com.bofry.databroker.core.component;

public interface IElasticsearchSinkAgent extends IAgent {

    ElasticsearchProperties getElasticsearchProperties();

    ElasticsearchBulkBuffer getBuffer();

}
