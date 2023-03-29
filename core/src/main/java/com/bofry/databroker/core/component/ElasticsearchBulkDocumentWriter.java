package com.bofry.databroker.core.component;

import com.bofry.databroker.core.dsl.DslLiteral;
import com.bofry.databroker.core.dsl.DslTemplate;
import com.bofry.databroker.core.dsl.MinifyDslTemplateLoader;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ElasticsearchBulkDocumentWriter extends ElasticsearchWriter implements IDocumentWriter {

    private final ElasticsearchProperties properties;
    private final ElasticsearchBulkBuffer buffer;
    private volatile transient ScheduledExecutorService scheduledThreadPool;
    private final DslTemplate template;


    @SneakyThrows
    public ElasticsearchBulkDocumentWriter(IElasticsearchSinkAgent agent) {
        super(agent);
        this.properties = agent.getElasticsearchProperties();
        this.buffer = agent.getBuffer();
        createTimer(properties.getBulkFlushInterval());
        this.template = DslTemplate.load(this.properties.getDsl(), MinifyDslTemplateLoader::load);
    }

    @SneakyThrows
    @Override
    public void write(OutputStreamContext ctx) {
        IValue entity = ctx.getEntity();

        String sourceJsonStr;
        String action;
        String body;
        String index;
        String docId;
        try {
            sourceJsonStr = entity.toJson();

            action = this.properties.getAction();
            String indexScript = this.properties.getIndex();
            String docIdScript = this.properties.getDocumentId();

            Map<String, Object> params = new HashMap<>();
            params.put("*", new DslLiteral(sourceJsonStr));
            body = this.template.apply(params);

            JRubyUtils.lock();
            Object o = EntityValue.unWarp((EntityValue) entity);
            index = JRubyUtils.evalScriptAsString(indexScript, o);
            docId = JRubyUtils.evalScriptAsString(docIdScript, o);
            JRubyUtils.unlock();

            ElasticsearchMetadataItem metadata = ElasticsearchMetadataItemFactory.create(action, docId, index, body);

            this.buffer.write(metadata);
            if (this.buffer.shouldFlush()) {
                flush();
            }
        } catch (Failure e) {
            // TODO 補充處理說明
            FailureHelper.setSource(e.getContent(), ctx.getSource().toString());
            super.throwFailure(e);
        } catch (Exception e) {
            super.throwFailure(e);
        }

    }

    @Override
    public void close() {
        // Ignore
    }

    @SneakyThrows
    public void flush() {
        if (!this.buffer.isEmpty()) {
            String outputBuffer = this.buffer.read();
            ElasticsearchRequestModel requestModel = ElasticsearchRequestModel.builder()
                    .endpoint("/_bulk")
                    .jsonEntity(outputBuffer)
                    .build();
            super.execute(requestModel);
        }
    }

    private void createTimer(Long delay) {
        if (this.scheduledThreadPool == null) {
            synchronized (this) {
                if (this.scheduledThreadPool == null) {
                    this.scheduledThreadPool = Executors.newScheduledThreadPool(5);
                    Runnable task = () -> {
                        try {
                            flush();
                        } catch (Exception e) {
                            if (e instanceof Failure){
                                Failure f = (Failure) e;
                                throwFailure(f);
                            } else {
                                throwFailure(e);
                            }
                        }
                    };
                    scheduledThreadPool.scheduleAtFixedRate(task, 1, delay, TimeUnit.SECONDS);
                }
            }
        }
    }

}
