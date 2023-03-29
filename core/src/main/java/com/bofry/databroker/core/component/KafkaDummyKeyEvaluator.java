package com.bofry.databroker.core.component;

public class KafkaDummyKeyEvaluator extends KafkaKeyEvaluator {

    @Override
    public String eval(Object entity) {
        return null;
    }
}
