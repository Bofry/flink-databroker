package com.bofry.databroker.core.serdes;

import com.bofry.databroker.core.script.TimestampPrecision;
import com.bofry.databroker.core.script.TimestampPrecisionConverter;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class DefaultAnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findDeserializationConverter(Annotated a) {
        TimestampPrecision property = a.getAnnotation(TimestampPrecision.class);
        if (property != null) {
            return new TimestampPrecisionConverter(property.source(), property.destination());
        }
        return super.findDeserializationConverter(a);
    }

}
