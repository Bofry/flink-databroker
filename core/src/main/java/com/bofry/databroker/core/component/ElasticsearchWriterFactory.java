package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

public class ElasticsearchWriterFactory {

    @SneakyThrows
    public static IDocumentWriter buildWriter(IAgent agent) {
        if (agent instanceof IElasticsearchSinkAgent) {
            IElasticsearchSinkAgent esAgent = (IElasticsearchSinkAgent) agent;
            if (esAgent.getElasticsearchProperties().getUseBulk()) {
                return new ElasticsearchBulkDocumentWriter(esAgent);
            }
            return new ElasticsearchStdDocumentWriter(esAgent);
        }
        // TODO 處理方式確認。
        throw new IllegalArgumentException();
    }

}
