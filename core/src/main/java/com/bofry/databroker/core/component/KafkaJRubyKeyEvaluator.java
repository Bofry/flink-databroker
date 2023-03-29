package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

public class KafkaJRubyKeyEvaluator extends KafkaKeyEvaluator {

    private final String keyScript;

    public KafkaJRubyKeyEvaluator(String keyScript) {
        this.keyScript = keyScript;
    }

    @SneakyThrows
    @Override
    public String eval(Object entity) {
        JRubyUtils.lock();
        String key = JRubyUtils.evalScriptAsString(this.keyScript, entity);
        JRubyUtils.unlock();
        return key;
    }
}
