package com.bofry.databroker.core.component;

import java.util.Map;

public class MetadataHelper {

    private static final String ID_TOPIC     = "topic";
    private static final String ID_PARTITION = "partition";
    private static final String ID_OFFSET    = "offset";
    private static final String ID_KEY       = "key";
    private static final String ID_VALUE     = "value";
    private static final String ID_HEADERS   = "headers";
    private static final String ID_DB        = "db";

    public static String getTopic(Map<String, Object> content) {
        return content.get(ID_TOPIC).toString();
    }

    public static void setTopic(Map<String, Object> content, Object value) {
        content.put(ID_TOPIC, value);
    }

    public static String getPartition(Map<String, Object> content) {
        return content.get(ID_PARTITION).toString();
    }

    public static void setPartition(Map<String, Object> content, Object value) {
        content.put(ID_PARTITION, value);
    }

    public static String getOffset(Map<String, Object> content) {
        return content.get(ID_OFFSET).toString();
    }

    public static void setOffset(Map<String, Object> content, Object value) {
        content.put(ID_OFFSET, value);
    }

    public static String getKey(Map<String, Object> content) {
        return content.get(ID_KEY).toString();
    }

    public static void setKey(Map<String, Object> content, Object value) {
        content.put(ID_KEY, value);
    }

    public static void setKeyIfNotExists(Map<String, Object> content, String value) {
        Object v = content.get(ID_KEY);
        if (v == null) {
            content.put(ID_KEY, value);
        }
    }

    public static String getValue(Map<String, Object> content) {
        return content.get(ID_VALUE).toString();
    }

    public static void setValue(Map<String, Object> content, Object value) {
        content.put(ID_VALUE, value);
    }

    public static String getHeaders(Map<String, Object> content) {
        return content.get(ID_HEADERS).toString();
    }

    public static void setHeaders(Map<String, Object> content, Object value) {
        content.put(ID_HEADERS, value);
    }

    public static String getIdDb(Map<String, Object> content) {
        return content.get(ID_DB).toString();
    }

    public static void setIdDb(Map<String, Object> content, Object value) {
        content.put(ID_DB, value);
    }
}
