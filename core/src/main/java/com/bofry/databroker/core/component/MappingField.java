package com.bofry.databroker.core.component;

import com.bofry.databroker.core.serdes.DefaultAssignerDeserializer;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.*;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MappingField implements Serializable {

    private String name;
    private String type;
    private String precision;
    private List<String> tag;
    private String source;
    @JsonAlias("default")
    @JsonDeserialize(using = DefaultAssignerDeserializer.class)
    private IValueAssigner defaultValueAssigner;

    @SneakyThrows
    public MappingField clone() {
        byte[] bytes;

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outStream);
        out.writeObject(this);
        out.close();

        bytes = outStream.toByteArray();
        outStream.close();

        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(inStream);
        MappingField cloned = (MappingField) in.readObject();
        in.close();
        inStream.close();
        return cloned;
    }

}
