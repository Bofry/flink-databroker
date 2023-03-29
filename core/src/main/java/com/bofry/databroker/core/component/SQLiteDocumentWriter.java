package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Properties;

@Log4j2
public class SQLiteDocumentWriter extends AbstractDocumentWriter {

    private volatile SQLiteWriter writer;
    private final Properties props;
    private final IAgent agent;

    public SQLiteDocumentWriter(IAgent agent) {
        this.agent = agent;
        this.props = JacksonUtils.OBJECT_MAPPER.convertValue(agent.getConfiguration().getConfig().get("properties"), Properties.class);
    }

    @SneakyThrows
    @Override
    public void write(OutputStreamContext ctx) {
        this.getWriter().execute(ctx);
    }

    public SQLiteWriter getWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    this.writer = new SQLiteWriter(this.props, this.agent);
                }
            }
        }
        return this.writer;
    }

}
