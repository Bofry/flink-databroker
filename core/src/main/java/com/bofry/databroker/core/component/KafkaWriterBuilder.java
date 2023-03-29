package com.bofry.databroker.core.component;

public class KafkaWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new KafkaDocumentWriter(agent);
    }
}
