package com.bofry.databroker.core.component;

public interface IDocumentWriter {

    void write(OutputStreamContext ctx);

    void close();

}
