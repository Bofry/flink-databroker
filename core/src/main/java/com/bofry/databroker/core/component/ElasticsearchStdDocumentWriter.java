package com.bofry.databroker.core.component;

import com.bofry.databroker.core.dsl.DslLiteral;
import com.bofry.databroker.core.dsl.DslTemplate;
import com.bofry.databroker.core.dsl.MinifyDslTemplateLoader;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class ElasticsearchStdDocumentWriter extends ElasticsearchWriter implements IDocumentWriter {

    private final ElasticsearchProperties properties;
    private final DslTemplate template;

    @SneakyThrows
    public ElasticsearchStdDocumentWriter(IElasticsearchSinkAgent agent) {
        super(agent);
        this.properties = agent.getElasticsearchProperties();
        this.template = DslTemplate.load(this.properties.getDsl(), MinifyDslTemplateLoader::load);
    }

    @SneakyThrows
    @Override
    public void write(OutputStreamContext ctx) {
        IValue entity = ctx.getEntity();

        String dsl;
        String endPoint = null;
        try {
            String sourceJsonStr = entity.toJson();

            String action = this.properties.getAction();
            String indexScript = this.properties.getIndex();
            String docIdScript = this.properties.getDocumentId();

            Map<String, Object> params = new HashMap<>();
            params.put("*", new DslLiteral(sourceJsonStr));
            dsl = this.template.apply(params);

            JRubyUtils.lock();
            Object o = EntityValue.unWarp((EntityValue) entity);
            String index = JRubyUtils.evalScriptAsString(indexScript, o);
            String docId = JRubyUtils.evalScriptAsString(docIdScript, o);
            JRubyUtils.unlock();
            if (!action.startsWith("_")) {
                action = "_" + action;
            }

            endPoint = String.format("/%s/%s/%s", index, action, docId);

            ElasticsearchRequestModel requestModel = ElasticsearchRequestModel.builder()
                    .endpoint(endPoint)
                    .jsonEntity(dsl)
                    .build();
            super.execute(requestModel);
        } catch (Failure e) {
            // TODO 補充處理說明
            FailureHelper.setSource(e.getContent(), ctx.getSource().toString());
            super.throwFailure(e);
        } catch (Exception e) {
            // TODO 補充處理說明
            super.throwFailure(e);
        }
    }

    @Override
    public void close() {
        // Ignore
    }

}
