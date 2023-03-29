package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractDocumentWriter implements IDocumentWriter {

    @Override
    public void close() {
        // ignore
    }
}
