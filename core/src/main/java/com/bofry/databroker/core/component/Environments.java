package com.bofry.databroker.core.component;

public class Environments {

    public static String get(String key) {
        return getEnvironment().get(key);
    }

    public static String expand(String input) {
        return getEnvironment().expand(input);
    }

    private static Environment getEnvironment(){
        return Environment.getInstance();
    }

}
