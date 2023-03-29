package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class FailureHelper {

    private static final String ID_KEY = "key";
    private static final String ID_VALUE = "value";
    private static final String ID_SOURCE = "source";

    public static String getKey(Map<String, Object> content) {
        Object v = content.get(ID_KEY);
        if (v != null) {
            return v.toString();
        }
        return null;
    }

    public static void setKey(Map<String, Object> content, String value) {
        content.put(ID_KEY, value);
    }

    public static void setKeyIfNotExists(Map<String, Object> content, String value) {
        Object v = content.get(ID_KEY);
        if (v == null) {
            content.put(ID_KEY, value);
        }
    }

    public static Object getValue(Map<String, Object> content) {
        Object v = content.get(ID_VALUE);
        if (v != null) {
            return v;
        }
        return null;
    }

    @SneakyThrows
    public static Map getValueToMap(Map<String, Object> content) {
        Object o = content.get(ID_VALUE);
        if (o != null) {
            Map<String, String> newMap = new HashMap<>();
            Map<String, String> m = null;
            if (o instanceof Map) {
                m = (Map) o;
            }
            if (o instanceof String) {
                m = JacksonUtils.OBJECT_MAPPER.readValue(o.toString(), Map.class);
            }
            if (m != null) {
                for (String strKey : m.keySet()) {
                    if (m.get(strKey) != null) {
                        newMap.put(strKey, String.valueOf(m.get(strKey)));
                    }
                }
                return newMap;
            }
        }
        return null;
    }

    @SneakyThrows
    public static String getValueToString(Map<String, Object> content) {
        Object o = content.get(ID_VALUE);
        if (o != null) {
            if (o instanceof String) {
                return content.toString();
            } else {
                return JacksonUtils.toJsonString(content);
            }
        }
        return null;
    }

    public static void setValue(Map<String, Object> content, Object value) {
        content.put(ID_VALUE, value);
    }

    public static Object getSource(Map<String, Object> content) {
        Object v = content.get(ID_SOURCE);
        if (v != null) {
            return v;
        }
        return null;
    }

    @SneakyThrows
    public static Map getSourceToMap(Map<String, Object> content) {
        Object o = content.get(ID_SOURCE);
        if (o != null) {
            Map<String, String> newMap = new HashMap<>();
            Map<String, String> m = null;
            if (o instanceof Map) {
                m = (Map) o;
            }
            if (o instanceof String) {
                m = JacksonUtils.OBJECT_MAPPER.readValue(o.toString(), Map.class);
            }
            if (m != null) {
                for (String strKey : m.keySet()) {
                    newMap.put(strKey, String.valueOf(m.get(strKey)));
                }
                return newMap;
            }
        }
        return null;
    }

    @SneakyThrows
    public static String getSourceToString(Map<String, Object> content) {
        Object o = content.get(ID_SOURCE);
        if (o != null) {
            if (o instanceof String) {
                return content.toString();
            } else {
                return JacksonUtils.toJsonString(content);
            }
        }
        return null;
    }

    public static void setSource(Map<String, Object> content, Object value) {
        content.put(ID_SOURCE, value);
    }

}
