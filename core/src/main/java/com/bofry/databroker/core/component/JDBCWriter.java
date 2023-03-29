package com.bofry.databroker.core.component;

import lombok.SneakyThrows;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDBCWriter implements Serializable {

    private static final String[] EMPTY_HANDLERS = new String[0];
    private static final String PLACEHOLDER_PATTERN = "%\\{([\\w\\*]+):?([\\w,]*)}";
    private final IAgent agent;
    private final Properties properties;
    private String sql;
    private List<Placeholder> placeholders;

    public JDBCWriter(Properties properties, IAgent agent) {
        this.properties = properties;
        this.agent = agent;
        // FIXME ping 服務 or 主機
        String s = properties.get("sql").toString();
        this.processSQLString(s);
    }

    public void execute(OutputStreamContext ctx) {

        String host = properties.get("host").toString();
        String user = properties.get("user").toString();
        String pass = properties.get("password").toString();

        Connection conn = null;
        String queryStr = "";
        try {
            conn = DriverManager.getConnection(host, user, pass);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            // 根據 names 去 source map 裡面去值並帶入
            // Mysql:
            // see also table 5.2 : https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
            // Postgresql
            // https://www.postgresql.org/docs/12/datatype.html
            // https://jdbc.postgresql.org/documentation/head/arrays.html
            for (int i = 0; i < this.placeholders.size(); i++) {
                Placeholder p = this.placeholders.get(i);
                if (p.isDocument()) {
                    Object v = EntityValue.unWarp((EntityValue) ctx.getEntity());
                    // FIXME Handler 現在簡單做，應該拉出去變成一個靜態物件和方法, 類似 hex, json
                    for (int j = 0; j < p.getHandlers().length; j++) {
                        String h = p.getHandlers()[j];
                        v = processDocumentParameter(v, h);
                    }
                    preparedStatement.setObject(i + 1, v);
                } else {
                    String columnName = p.getName();
                    Object v = ctx.getModel().getDeclaredField(columnName).get(EntityValue.unWarp((EntityValue) ctx.getEntity()));
                    // FIXME 目前是沒有加入 Handler, 之後可以實作。
                    for (int j = 0; j < p.getHandlers().length; j++) {
                        String h = p.getHandlers()[j];
                        v = processColumnParameter(v, columnName, h);
                    }
                    preparedStatement.setObject(i + 1, v);
                }
            }
            // FIXME Postgrsql 無法印出執行的 SQL
            // queryStr = ((ClientPreparedStatement) preparedStatement).asSql();
            preparedStatement.execute();
        } catch (Exception e) {
            // TODO 補充處理說明
            Failure f = new JDBCWriterFailure(e);
            FailureHelper.setKey(f.getContent(), MetadataHelper.getKey(ctx.getMetadata()));
            // FIXME java.lang.NullPointerException: null getValue
            // 此 Value 適用於 Retry 的時候要帶入到 f.getContent()內的 value (目前尚未實作)。
            // FailureHelper.setValue(f.getContent(), MetadataHelper.getValue(ctx.getMetadata()));
            FailureHelper.setSource(f.getContent(), ctx.getSource().toString());
            this.throwFailure(f);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                this.throwFailure(e);
            }
        }

    }

    private void processSQLString(String input) {

        Pattern expr = Pattern.compile(PLACEHOLDER_PATTERN);
        Matcher matcher = expr.matcher(input);

        List<Placeholder> placeholders = new ArrayList<>();

        while (matcher.find()) {
            String token = matcher.group(0);
            String name = matcher.group(1);
            String[] handlers = EMPTY_HANDLERS;
            {
                String str = matcher.group(2);
                if (str != null) {
                    handlers = str.split(",");
                }
            }
            Placeholder p = Placeholder.builder()
                    .name(name)
                    .handlers(handlers)
                    .build();

            placeholders.add(p);

            Pattern subexpr = Pattern.compile(Pattern.quote(token));
            input = subexpr.matcher(input).replaceAll("?");
        }

        this.sql = input;
        this.placeholders = placeholders;
    }

    @SneakyThrows
    protected Object processDocumentParameter(Object value, String handler) {
        Object v = value;
        if (handler.equals("json")) {
            v = JacksonUtils.toJsonString(value);
        } else {
            throw new RuntimeException(String.format("Unknown handler '%s'", handler));
        }
        return v;
    }

    @SneakyThrows
    protected Object processColumnParameter(Object value, String columnName, String handler) {
        Object v = value;
        return v;
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }

}
