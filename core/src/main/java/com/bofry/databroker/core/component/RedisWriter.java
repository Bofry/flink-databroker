package com.bofry.databroker.core.component;

import com.bofry.databroker.core.redis.stream.LettuceStreamAdapter;
import com.bofry.databroker.core.redis.stream.LettuceStreamConnection;
import com.bofry.databroker.core.redis.stream.LettuceStreamConnectionFactory;
import com.bofry.databroker.core.redis.stream.RedisStreamProducer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

public class RedisWriter implements Serializable {

    private final IAgent agent;
    private RedisClient client;
    private LettuceStreamConnection connection;
    private final String redisHost;
    private final int redisPort;
    private final int redisDB;
    private final String stream;

    private LettuceStreamAdapter adapter;
    private RedisStreamProducer producer;

    public RedisWriter(Properties properties, IAgent agent) {
        this.agent = agent;

        this.redisHost = properties.get("redis.host").toString();
        this.redisPort = Integer.parseInt(properties.get("redis.port").toString());
        this.redisDB = Integer.parseInt(properties.get("redis.db").toString());
        this.stream = properties.get("redis.stream.name").toString();

        RedisURI redisUri = RedisURI.Builder
                .redis(redisHost)
                .withPort(redisPort)
                .withDatabase(redisDB)
                .build();

        client = RedisClient.create(redisUri);

        if (this.connection == null) {
            synchronized (this) {
                if (this.connection == null) {
                    connection = LettuceStreamConnectionFactory.create(client);
                    connection.connect();
                }
            }
        }

        if (this.adapter == null) {
            synchronized (this) {
                if (this.adapter == null) {
                    this.adapter = new LettuceStreamAdapter(connection);
                }
            }
        }

        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    this.producer = new RedisStreamProducer(adapter);
                }
            }
        }
    }

    public void execute(Map<String, String> content) {
        try {
            this.producer.produce(stream, content);
        } catch (Exception e) {
            Failure f = new RedisWriterFailure(e);
            FailureHelper.setValue(f.getContent(), content);
            throwFailure(f);
        }
    }

    private void throwFailure(Exception e) {
        this.agent.throwFailure(e);
    }


}
