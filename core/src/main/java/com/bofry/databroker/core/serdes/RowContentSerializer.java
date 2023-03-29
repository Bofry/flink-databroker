package com.bofry.databroker.core.serdes;

import com.bofry.databroker.core.component.RawContent;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RowContentSerializer extends JsonSerializer<RawContent> {
    @Override
    public void serialize(RawContent value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.getData() == null) {
            gen.writeNull();
        } else {
            gen.writeRawValue(value.getData());
        }
    }
}
