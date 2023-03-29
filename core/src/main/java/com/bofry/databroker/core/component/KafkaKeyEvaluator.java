package com.bofry.databroker.core.component;

public abstract class KafkaKeyEvaluator {

    protected abstract String eval(Object entity);

}
