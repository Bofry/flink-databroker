package com.bofry.databroker.core.component;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public final class SourceConfiguration implements Serializable {

    private String type;
    private String route;
    private Map<String, Object> config;
    private List<MappingField> metadata = new ArrayList<>();
    private Map<String, MappingField> mappingTable;

    public Map<String, MappingField> getMappingTable() {
        if (this.mappingTable == null) {
            this.mappingTable = this.getMetadata().stream()
                    .collect(Collectors.toMap(MappingField::getName, MappingField::clone));
        }
        return this.mappingTable;
    }

}

