package com.bofry.databroker.core.component;

import lombok.Builder;
import lombok.Data;

@Builder
public class ElasticsearchMetadataItem {

    private String metadata;
    private String body;

//    public String toDSL() {
//        return metadata + "\n" +
//                body + "\n";
//    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return metadata + "\n" +
                body + "\n";
    }
}
