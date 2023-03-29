package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.Properties;

@Log4j2
public class RedisDocumentWriter extends AbstractDocumentWriter implements IFailureDocumentWriter {

    private volatile RedisWriter writer;
    private final Properties props;
    private final IAgent agent;

    public RedisDocumentWriter(IAgent agent) {
        this.agent = agent;
        this.props = JacksonUtils.OBJECT_MAPPER.convertValue(agent.getConfiguration().getConfig().get("properties"), Properties.class);
    }

    @SneakyThrows
    @Override
    public void write(OutputStreamContext ctx) {
        try {
            if (ctx.getContentSourceType().equals("redis")) {
                Map map = (Map) ctx.getRawContent();
                this.getWriter().execute(map);
                return;
            }
            this.getWriter().execute(ctx.getEntity().toMap());
        } catch (Exception e) {
            // TODO 秀 source 或是 value
            this.throwFailure(e);
        }
    }

    @Override
    public void write(FailureContext f) {
        Map value;
        switch (f.getAction()) {
            case RETRY:
                value = FailureHelper.getValueToMap(f.getContent());
                break;
            case Fail:
                value = FailureHelper.getValueToMap(f.getContent());
                if (value == null) {
                    value = FailureHelper.getSourceToMap(f.getContent());
                }
                break;
            default:
                return;
        }
        try {
            this.getWriter().execute(value);
        } catch (Exception e) {
            // TODO 秀 source 或是 value
            this.throwFailure(e);
        }
    }

    public RedisWriter getWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    this.writer = new RedisWriter(this.props, this.agent);
                }
            }
        }
        return this.writer;
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }
}
