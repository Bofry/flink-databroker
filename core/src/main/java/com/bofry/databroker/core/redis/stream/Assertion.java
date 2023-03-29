package com.bofry.databroker.core.redis.stream;

import java.util.Map;

final class Assertion {
    public static boolean isNullOrEmpty(String input) {
        return (input == null) || (input.length() == 0);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map.size() == 0;
    }
}
