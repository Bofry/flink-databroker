package com.bofry.databroker.core.component;

public class PostgresqlWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new JDBCDocumentWriter(agent);
    }
}
