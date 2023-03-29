package com.bofry.databroker.core.script;

import com.bofry.databroker.core.script.type.Timestamp;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JavaType;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.util.Converter;

import java.util.HashMap;
import java.util.Map;

public class TimestampPrecisionConverter implements Converter<Timestamp, Timestamp> {

    /**
     * 時間單位：
     * 皮秒(psec) unused.
     * 奈秒(nsec) unused.
     * 微秒(usec) unused.
     * 毫秒(msec)
     * 秒(sec)
     * 分(minute) unused.
     * 時(hour) unused.
     * 日(day) unused.
     * 月(month) unused.
     * 年(year) unused.
     */
    private static final Map<String, Long> timestampTable = new HashMap<>();

    static {
        timestampTable.put("sec", 1000L);
        timestampTable.put("msec", 1L);
    }

    private final long srcPrecision;
    private final long dstPrecision;

    public TimestampPrecisionConverter(String srcPrecision, String dstPrecision) {
        // TODO 錯誤參數處理
        this.srcPrecision = timestampTable.get(srcPrecision);
        this.dstPrecision = timestampTable.get(dstPrecision);
    }

    /**
     input | src | dst  | output
     1    |  s  |  ms  | 1000
     1000 | ms  |  s   | 1
     1    |  s  |  s   | 1
     1000 | ms  |  ms  | 1000

     公式：output = input * srcPrecision / dstPrecision;
     **/
    @Override
    public Timestamp convert(Timestamp timestamp) {
        long src = timestamp.getTime();
        long result = src * this.srcPrecision / this.dstPrecision;
        return new Timestamp(result);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructFromCanonical(Timestamp.class.getName());
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructFromCanonical(Timestamp.class.getName());
    }

}
