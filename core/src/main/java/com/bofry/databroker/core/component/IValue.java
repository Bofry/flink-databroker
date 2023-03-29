package com.bofry.databroker.core.component;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Serializable;
import java.util.Map;

public interface IValue extends Serializable {

    String toJson() throws JsonProcessingException;

    Map toMap() throws JsonProcessingException;

    Object toEntity(Class clazz);

}
