package com.bofry.databroker.core.component;

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

public class SQLiteWriter implements Serializable {

    private final Properties properties;
    private final IAgent agent;

    public SQLiteWriter(Properties properties, IAgent agent) {
        this.properties = properties;
        this.agent = agent;
    }

    public void execute(OutputStreamContext ctx) {

        Connection conn = null;
        try {
            Object o = EntityValue.unWarp((EntityValue) ctx.getEntity());
            String host = JRubyUtils.evalScriptAsString(properties.get("host").toString(), o);
            String user = properties.get("user").toString();
            String pass = properties.get("password").toString();
            String sql = properties.get("sql").toString();

            String[] sqls = sql.split("(?<=;)");

            for (String s : sqls) {

                // 取出要汰換的 value 名稱，並將其 SQL 語法換成 ? 符號
                String pattern = "%\\{[\\w]+}";
                Pattern expr = Pattern.compile(pattern);
                Matcher matcher = expr.matcher(s);

                List<String> names = new ArrayList<>();
                while (matcher.find()) {
                    String name = matcher.group(0);
                    names.add(name.substring(2, name.length() - 1));
                    Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
                    s = subexpr.matcher(s).replaceAll("?");
                }

                conn = DriverManager.getConnection(host, user, pass);
                PreparedStatement preparedStatement = conn.prepareStatement(s);

                for (int i = 0; i < names.size(); i++) {
                    Object v = ctx.getModel().getDeclaredField(names.get(i)).get(ctx.getEntity());
                    preparedStatement.setObject(i + 1, v);
                }
                preparedStatement.execute();

            }
        } catch (Exception e) {
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

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }
}
