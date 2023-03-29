package com.bofry.databroker.core.component;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticsearchProperties implements Serializable {

    private static final Boolean DEFAULT_USE_BULK = false;
    private static final Long DEFAULT_BULK_FLUSH_INTERVAL = 10L;
    private static final Integer DEFAULT_BULK_FLUSH_CAPACITY = 50;
    private static final Integer DEFAULT_BULK_FLUSH_MAX_SIZE = 2097152;

    /**
     * elasticsearch host
     */
    private String host;
    /**
     * elasticsearch user
     */
    private String user;
    /**
     * elasticsearch password
     */
    private String password;
    /**
     * use bulk api true/false
     * default: false
     */
    @JsonAlias("use_bulk")
    private Boolean useBulk;
    /**
     * bulk interval
     * default: 10 sec
     */
    @JsonAlias("bulk_flush_interval")
    private Long bulkFlushInterval;
    /**
     * bulk api capacity
     * default: 50
     */
    @JsonAlias("bulk_flush_capacity")
    private Integer bulkFlushCapacity;
    /**
     * bulk api max content bytes
     * default: 2097152
     */
    @JsonAlias("bulk_flush_max_size")
    private Integer bulkFlushMaxSize;
    /**
     * GET, POST
     */
    private String method;
    /**
     * insert, update, delete
     */
    private String action;
    /**
     * JRuby script
     * example: "gaas-round-${Environment}-#{ctx.round.to_s[0..5]}"
     */
    private String index;
    /**
     * JRuby script
     * example: "round:#{ctx.round}:round_type:#{ctx.round_type}"
     */
    @JsonAlias("document_id")
    private String documentId;
    /**
     * elasticsearch dsl string
     */
    private String dsl;

    public Boolean getUseBulk() {
        if (this.useBulk == null) {
            this.useBulk = DEFAULT_USE_BULK;
        }
        return useBulk;
    }

    public Long getBulkFlushInterval() {
        if (this.bulkFlushInterval == null || this.bulkFlushInterval <= 0) {
            this.bulkFlushInterval = DEFAULT_BULK_FLUSH_INTERVAL;
        }
        return bulkFlushInterval;
    }

    public Integer getBulkFlushCapacity() {
        if (this.bulkFlushCapacity == null || this.bulkFlushCapacity <= 0) {
            this.bulkFlushCapacity = DEFAULT_BULK_FLUSH_CAPACITY;
        }
        return this.bulkFlushCapacity;
    }

    public Integer getBulkFlushMaxSize() {
        if (this.bulkFlushMaxSize == null || this.bulkFlushMaxSize <= 0) {
            this.bulkFlushMaxSize = DEFAULT_BULK_FLUSH_MAX_SIZE;
        }
        return this.bulkFlushMaxSize;
    }
}
