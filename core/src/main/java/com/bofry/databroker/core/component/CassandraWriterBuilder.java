package com.bofry.databroker.core.component;

public class CassandraWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new CassandraDocumentWriter(agent);
    }
}
