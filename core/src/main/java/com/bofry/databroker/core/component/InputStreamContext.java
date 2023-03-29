package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class InputStreamContext {

    /**
     * 用於建立/取得 Models 內的 Model。
     * (避免多執行緒造成取得不同的問題)
     */
    private String id;
    /**
     * 多對多對應處理的名稱
     * (NameMatcher)
     */
    private String route;
    /**
     * Src mapping table.
     */
    private Map<String, MappingField> mappingTable;
    /**
     * 目前僅針對 Kafka：
     * 除了主要 source 外，其他的資訊都可以塞入至此
     * topic, partition, offset, key, headers
     */
    private Map<String, Object> metadata;
    /**
     * 主要的 kafka message or source
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
