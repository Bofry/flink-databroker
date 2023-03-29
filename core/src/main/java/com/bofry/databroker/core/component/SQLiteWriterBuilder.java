package com.bofry.databroker.core.component;

public class SQLiteWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new SQLiteDocumentWriter(agent);
    }
}
