package com.bofry.databroker.core.component;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class ElasticsearchSink extends BaseSink implements IElasticsearchSinkAgent {

    private static final long serialVersionUID = 6473865271591940596L;

    private final IObjectDescriptor descriptor;
    private volatile ElasticsearchProperties elasticsearchProperties;
    private volatile IDocumentWriter writer;
    private final String stateStoreKey;
    private volatile ElasticsearchBulkBuffer buffer;

    public ElasticsearchSink(SinkConfiguration conf) {
        super(conf);
        this.descriptor = new DescriptorDecorator(null);
        this.stateStoreKey = conf.getStateStoreKey();
        this.createElasticsearchProperties();
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

    @Override
    protected void processStreamData(OutputStreamContext ctx) {
        createWriter();
        this.writer.write(ctx);
    }

    private void createWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    this.writer = ElasticsearchWriterFactory.buildWriter(this);
                }
            }
        }
    }

    @SneakyThrows
    private void createElasticsearchProperties() {
        if (this.elasticsearchProperties == null) {
            synchronized (this) {
                if (this.elasticsearchProperties == null) {
                    this.elasticsearchProperties = JacksonUtils.OBJECT_MAPPER.convertValue(this.getConfiguration().getConfig().get("properties"), ElasticsearchProperties.class);
                }
            }
        }
    }

    @Override
    public ElasticsearchProperties getElasticsearchProperties() {
        return this.elasticsearchProperties;
    }

    @Override
    public ElasticsearchBulkBuffer getBuffer() {
        if (this.buffer == null) {
            synchronized (this) {
                if (this.buffer == null) {
                    StateStoreManager.lock();
                    Object stateStore = StateStoreManager.get(this.stateStoreKey);
                    if (stateStore == null) {
                        StateStoreManager.set(this.stateStoreKey, new ElasticsearchBulkBuffer(this.elasticsearchProperties));
                        this.buffer = (ElasticsearchBulkBuffer) StateStoreManager.get(this.stateStoreKey);
                    }
                    StateStoreManager.unlock();
                }
            }
        }
        return this.buffer;
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

