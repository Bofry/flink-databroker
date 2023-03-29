package com.bofry.databroker.core.dsl;

public interface DslTemplateLoader {
    DslTemplate load(String input) throws Exception;
}
