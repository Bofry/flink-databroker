package com.bofry.databroker.core.component;

public class RedisWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new RedisDocumentWriter(agent);
    }
}
