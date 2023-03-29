package com.bofry.databroker.core.serdes;

import com.bofry.databroker.core.component.RawContent;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class RowContentDeserializer extends JsonDeserializer<RawContent> {

    @Override
    public RawContent deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String data = p.readValueAsTree().toString();
        String name = p.currentName();
        return RawContent.builder()
                .name(name)
                .data(data)
                .build();
    }
}
