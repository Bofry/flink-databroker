package com.bofry.databroker.core.component;

import lombok.Data;

import java.util.Map;

@Data
public class OutputStreamContext {

    public OutputStreamContext(IValue entity, Class model, InputStreamContext ctx) {
        this.entity = entity;
        this.model = model;
        this.metadata = ctx.getMetadata();
        this.source = ctx.getSource();
        this.contentSourceType = ctx.getContentSourceType();
        this.rawContent = ctx.getRawContent();
    }

    /**
     * 動態產生的 Object
     * (使用srcMappingTable產生且已塞入source值)
     */
    private IValue entity;
    /**
     * 動態產生的 Class
     * (部分I/O Writer用於判斷塞入 parameters, 例：JDBC)
     */
    private Class model;
    /**
     * 原始的 dataContext.metadata
     * 目前僅針對 Kafka：
     * topic, partition, offset, key, headers
     */
    private Map<String, Object> metadata;
    /**
     * 原始的 dataContext.source or message
     */
    private IValue source;
    /**
     * 來源的Type
     */
    private String contentSourceType;

    /**
     * 最原始的基本資料型態
     */
    private Object rawContent;

}
