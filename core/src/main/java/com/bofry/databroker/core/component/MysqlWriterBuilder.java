package com.bofry.databroker.core.component;

public class MysqlWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new JDBCDocumentWriter(agent);
    }
}
