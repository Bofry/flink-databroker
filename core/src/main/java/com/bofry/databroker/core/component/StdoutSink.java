package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class StdoutSink extends BaseSink implements IAgent {

    private static final long serialVersionUID = 1237374235805831452L;

    private final IObjectDescriptor descriptor;
    private volatile IDocumentWriter writer;

    public StdoutSink(SinkConfiguration conf) {
        super(conf);
        this.descriptor = new DescriptorDecorator(null);
    }

    @Override
    protected IObjectDescriptor getDescriptor() {
        return this.descriptor;
    }

    @SneakyThrows
    @Override
    protected Model createModel(Map<String, MappingField> srcMapping, IObjectDescriptor descriptor) {
        return new StdModel(this.getConfiguration().getConfigClassName(), this.getConfiguration().getMappingTable(), srcMapping, descriptor);
    }

    @SneakyThrows
    @Override
    protected void processStreamData(OutputStreamContext ctx) {
        createWriter();
        this.writer.write(ctx);
    }

    private void createWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    StdoutWriterBuilder builder = new StdoutWriterBuilder();
                    this.writer = builder.buildWriter(this);
                }
            }
        }
    }

    @Override
    public SinkConfiguration getConfiguration() {
        return super.getConfiguration();
    }

    @Override
    public void throwFailure(Exception e) {
        super.throwFailure(e);
    }
}

