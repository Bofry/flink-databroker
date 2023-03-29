package com.bofry.databroker.core.component;

import com.bofry.databroker.core.serdes.DefaultAnnotationIntrospector;
import com.bofry.databroker.core.serdes.RowContentDeserializer;
import com.bofry.databroker.core.serdes.RowContentSerializer;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Data
public class JacksonUtils {

    public static final ObjectMapper YAML_OBJECT_MAPPER;
    public static final ObjectMapper OBJECT_MAPPER;

    static {
        YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
        YAML_OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        YAML_OBJECT_MAPPER.findAndRegisterModules();
        YAML_OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(RawContent.class, new RowContentDeserializer());
        module.addSerializer(RawContent.class, new RowContentSerializer());

        OBJECT_MAPPER.registerModule(module);
    }

    public static Object toObject(String content, Class clazz) throws JsonProcessingException {
        OBJECT_MAPPER.setAnnotationIntrospector(new DefaultAnnotationIntrospector());
        return OBJECT_MAPPER.readValue(content, clazz);
    }

    public static String toJsonString(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

}
