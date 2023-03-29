package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class KafkaSink extends BaseSink implements IAgent {

    private static final long serialVersionUID = 6473865271591940596L;

    private final IObjectDescriptor descriptor;
    private volatile IDocumentWriter writer;

    public KafkaSink(SinkConfiguration conf) {
        super(conf);
        this.descriptor = new DescriptorDecorator(new KafkaDescriptor());
    }

    @Override
    protected IObjectDescriptor getDescriptor() {
        return this.descriptor;
    }

    @SneakyThrows
    @Override
    protected Model createModel(Map<String, MappingField> srcMapping, IObjectDescriptor descriptor) {
        return new StdModel(super.getConfiguration().getConfigClassName(), super.getConfiguration().getMappingTable(), srcMapping, descriptor);
    }

    @Override
    protected void processStreamData(OutputStreamContext ctx) {
        createWriter();
        this.writer.write(ctx);
    }

    private void createWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    KafkaWriterBuilder builder = new KafkaWriterBuilder();
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

