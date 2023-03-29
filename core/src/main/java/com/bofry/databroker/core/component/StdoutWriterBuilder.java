package com.bofry.databroker.core.component;

public class StdoutWriterBuilder implements IWriterBuilder {

    @Override
    public IDocumentWriter buildWriter(IAgent agent) {
        return new StdoutDocumentWriter(agent);
    }
}
