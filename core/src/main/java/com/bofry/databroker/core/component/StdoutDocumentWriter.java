package com.bofry.databroker.core.component;

import lombok.extern.log4j.Log4j2;

import java.util.Properties;

@Log4j2
public class StdoutDocumentWriter extends AbstractDocumentWriter {

    private final String prefix;
    private final IAgent agent;

    public StdoutDocumentWriter(IAgent agent) {
        this.agent = agent;
        SinkConfiguration conf = (SinkConfiguration) agent.getConfiguration();

        this.prefix = conf.getConfig().get("prefix").toString();
        Properties props = JacksonUtils.OBJECT_MAPPER.convertValue(conf.getConfig().get("properties"), Properties.class);
    }

    @Override
    public void write(OutputStreamContext ctx) {
        try {
            IValue entity = ctx.getEntity();
//            System.out.println(this.prefix + entity.toJson());
            log.info(String.format("%s%s", this.prefix, entity.toJson()));
        } catch (Exception e) {
            // TODO 秀 source 或是 value
            this.throwFailure(e);
        }
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }
}
