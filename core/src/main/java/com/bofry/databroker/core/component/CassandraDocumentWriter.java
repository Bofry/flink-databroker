package com.bofry.databroker.core.component;

import com.datastax.driver.core.SimpleStatement;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class CassandraDocumentWriter extends AbstractDocumentWriter {

    private volatile CassandraWriter writer;
    private final Map<String, Object> props;
    private final IAgent agent;

    public CassandraDocumentWriter(IAgent agent) {
        this.agent = agent;
        this.props = JacksonUtils.OBJECT_MAPPER.convertValue(agent.getConfiguration().getConfig().get("properties"), Map.class);
    }

    @Override
    public void write(OutputStreamContext ctx) {
        String value = null;
        try {
            IValue entity = ctx.getEntity();
            value = entity.toJson();

            String keyspace = this.props.get("keyspace").toString();
            String table = this.props.get("table").toString();
            String query = String.format("INSERT INTO %s.%s JSON ?", keyspace, table);

            ensureWriter();
            SimpleStatement stmt = new SimpleStatement(query, value);

            this.writer.write(stmt);
        } catch (Failure f) {
            FailureHelper.setValue(f.getContent(), value);
            FailureHelper.setSource(f.getContent(), ctx.getSource().toString());
            this.throwFailure(f);
        } catch (Exception e) {
            this.throwFailure(e);
        }
    }

    private void ensureWriter() {
        if (this.writer == null) {
            synchronized (this) {
                if (this.writer == null) {
                    this.writer = new CassandraWriter(this.props);
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void close() {
        super.close();
        writer.close();
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }
}
