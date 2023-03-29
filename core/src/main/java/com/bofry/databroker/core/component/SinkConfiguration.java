package com.bofry.databroker.core.component;

import com.bofry.databroker.core.serdes.NameMatcherDeserializer;
import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public final class SinkConfiguration implements IConfiguration {

    private final String KEY_CLASS_NAME = "class_name";

    private String id;
    private String type;
    @JsonAlias("route")
    @JsonDeserialize(using = NameMatcherDeserializer.class)
    private INameMatcher routeNameMatcher;
    private String filter;
    private Map<String, Object> config;
    private List<MappingField> mapping = new ArrayList<>();
    private Map<String, MappingField> mappingTable;
    @JsonAlias("on_retry")
    private WriterConfiguration onRetry;
    @JsonAlias("on_failure")
    private WriterConfiguration onFailure;
    private IFailureHandler failureHandler;

    public Map<String, MappingField> getMappingTable() {
        if (this.mappingTable == null) {
            this.mappingTable = this.getMapping().stream()
                    .collect(Collectors.toMap(MappingField::getName, MappingField::clone));
        }
        return this.mappingTable;
    }

    public void config(Configuration c) {
        if (!this.getConfig().containsKey(this.KEY_CLASS_NAME)) {
            this.getConfig().put(this.KEY_CLASS_NAME, String.format("%s.%s_%s.DataModel", c.getNamespace(), this.type, getId()));
        }
    }

    public String getConfigClassName() {
        return this.getConfig().get(this.KEY_CLASS_NAME).toString();
    }

    public String getId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return this.id;
    }

    /**
     * Used in StoreKey
     */
    public String getStateStoreKey() {
        return type + ":" + getId();
    }

    public INameMatcher getRouteNameMatcher() {
        if (this.routeNameMatcher == null) {
            this.routeNameMatcher = new IgnoreMatcher();
        }
        return this.routeNameMatcher;
    }

    public IFailureHandler getFailureHandler() {
        if (this.failureHandler == null) {
            synchronized (this) {
                if (this.failureHandler == null) {
                    FailureManager manager = new FailureManager();
                    // Add Default ExceptionFailureHandler
                    manager.addHandler(FailureManager.DEFAULT_PRIORITY, new ExceptionFailureHandler());
                    if (this.onFailure != null) {
                        manager.addHandler(FailureManager.FAILURE_PRIORITY, new FailureHandler(this.onFailure));
                    }
                    if (this.onRetry != null) {
                        manager.addHandler(FailureManager.RETRYABLE_PRIORITY, new RetryableFailureHandler(this.onRetry));
                    }
                    this.failureHandler = manager;
                }
            }
        }
        return this.failureHandler;
    }

    public String getFilter() {
        if (this.filter != null) {
            return this.filter.trim();
        }
        return null;
    }
}
