package com.bofry.databroker.core.component;

import com.bofry.databroker.core.validator.CompositeValidator;
import com.bofry.databroker.core.validator.IValidator;
import lombok.SneakyThrows;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseSink extends RichSinkFunction<InputStreamContext> {

    private final SinkConfiguration configuration;
    private IObjectDescriptor descriptor;
    private final Map<String, Model> models = new HashMap<>();
    private volatile IValidator compositeValidator;
    private IFailureHandler failureHandler;
    private final String filterScript;

    public BaseSink(SinkConfiguration conf) {
        this.configuration = conf;
        this.filterScript = this.configuration.getFilter();
    }

    @Override
    public void invoke(InputStreamContext input, Context context) {

        if (!this.configuration.getRouteNameMatcher().match(input.getRoute())) {
            return;
        }

        // 取得 Sink 所需參數
        String id = input.getId();
        IValue source = input.getSource();
        Map<String, MappingField> srcMapping = input.getMappingTable();
        Map<String, MappingField> modelMapping = configuration.getMappingTable();

        if(modelMapping == null || modelMapping.size() == 0) {
            // FIXME 要不要動態產生 model ， Redis to Kafka
            // 允許沒有 modelMapping 的調整。

            // 準備 Context
            OutputStreamContext output = new OutputStreamContext(
                    source,
                    null,
                    input);

            processStreamData(output);
        } else {

            // TODO
//            if(modelMapping == null){
//                throw new RuntimeException("missing model mapping");
//            }

            // 取得 ObjectDescriptor
            this.descriptor = getDescriptor();
            // 取得 Model
            Model model = this.getModel(id, srcMapping);
            // 建立 CompositeValidator
            this.createCompositeValidator();

            try {
                // 動態產生 Object(content)
                Object entity = model.fillFromJson(source.toJson());
                // 過濾資料
                if (!shouldProcess(this.filterScript, entity)) {
                    return;
                }
                // 驗證欄位
                compositeValidator.validate(entity);
                // 準備 Context
                OutputStreamContext output = new OutputStreamContext(
                        new EntityValue(entity),
                        model.getModelClass(),
                        input);
                // 處理請求
                processStreamData(output);
            } catch (Exception e) {
                MappingFieldValidationFailure f = new MappingFieldValidationFailure(e);
                try {
                    FailureHelper.setSource(f.getContent(), source.toJson());
                } catch (JsonProcessingException jsonProcessingException) {
                    // FIXME
                    jsonProcessingException.printStackTrace();
                }
                this.throwFailure(f);
            }
        }

    }

    private Model getModel(String id, Map<String, MappingField> srcMapping) {
        Model m = this.models.get(id);
        if (m == null) {
            synchronized (this) {
                m = this.models.get(id);
                if (m == null) {
                    m = createModel(srcMapping, this.descriptor);
                    this.models.put(id, m);
                }
            }
        }
        return m;
    }

    private void createCompositeValidator() {
        if (this.compositeValidator == null) {
            synchronized (this) {
                if (this.compositeValidator == null) {
                    this.compositeValidator = new CompositeValidator(this.configuration);
                }
            }
        }
    }

    protected void throwFailure(Exception e) {
        if (this.failureHandler == null) {
            synchronized (this) {
                if (this.failureHandler == null) {
                    this.failureHandler = this.configuration.getFailureHandler();
                }
            }
        }
        this.failureHandler.process(e);
    }

    protected abstract IObjectDescriptor getDescriptor();

    protected abstract Model createModel(Map<String, MappingField> srcMapping, IObjectDescriptor descriptor);

    protected abstract void processStreamData(OutputStreamContext ctx);

    protected SinkConfiguration getConfiguration() {
        return configuration;
    }

    public IFailureHandler getFailureHandler() {
        return failureHandler;
    }

    protected void closeImpl() {
    }

    @Override
    public final void close() throws Exception {
        super.close();
        this.closeImpl();
    }

    @SneakyThrows
    private boolean shouldProcess(String script, Object entity) {
        if (script == null || script.length() == 0) {
            return true;
        }
        JRubyUtils.lock();
        boolean result = JRubyUtils.evalScriptAsBool(script, entity);
        JRubyUtils.unlock();
        return result;
    }

    // TODO isSupportMappingIgnore()
    // protected abstract boolean isSupportMappingIgnore();

}
