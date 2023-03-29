package com.bofry.databroker.core.component;

import com.bofry.databroker.core.redis.stream.*;
import com.bofry.databroker.core.redis.stream.*;
import io.lettuce.core.RedisBusyException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class RedisSource extends RichParallelSourceFunction<InputStreamContext> {

    private final String COMMIT_ACTION_CODE = "commit";
    private final String DELETE_ACTION_CODE = "delete";

    private RedisClient client;
    private LettuceStreamConnection connection;
    private RedisStreamConsumer consumer;
    private final String id;
    private final String type;
    private final String route;
    private final Map<String, MappingField> mappingTable;
    private final String host;
    private final int port;
    private final int db;
    private final String group;
    private final String stream;
    private int maxInFlight = 0;
    private Duration maxPollingTimeout = Duration.ZERO;
    private Duration claimMinIdleTime = Duration.ofSeconds(30);
    private Duration idlingTimeout = Duration.ZERO;
    private int claimSensitivity = 1;
    private int claimOccurrenceRate = 5;
    private String commitAction = COMMIT_ACTION_CODE;
    private Duration commitInterval = Duration.ofMillis(1000);
    private int concurrency = 1;

    public RedisSource(SourceConfiguration conf) {
        this.id = UUID.randomUUID().toString();
        this.type = conf.getType();
        this.route = conf.getRoute();
        this.mappingTable = conf.getMappingTable();

        Properties props = JacksonUtils.OBJECT_MAPPER.convertValue(conf.getConfig().get("properties"), Properties.class);
        initialize(props);
        this.host = props.get("host").toString();
        this.port = Integer.parseInt(props.get("port").toString());
        this.db = Integer.parseInt(props.get("db").toString());
        this.group = props.get("stream.group").toString();
        this.stream = props.get("stream.name").toString();
    }

    @Override
    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
        if (this.connection == null) {
            synchronized (this) {
                if (this.connection == null) {
                    super.open(parameters);

                    RedisURI redisUri = RedisURI.Builder
                            .redis(host)
                            .withPort(port)
                            .withDatabase(db)
                            .build();

                    this.client = RedisClient.create(redisUri);
                    this.connection = LettuceStreamConnectionFactory.create(client);
                    this.connection.connect();

                    LettuceStreamAdapter adapter = new LettuceStreamAdapter(connection);
                    RedisStreamAdmin admin = new RedisStreamAdmin(adapter);

                    try {
                        admin.createConsumerGroup(RedisStreamOffset.fromZero(stream), group,
                                RedisStreamGroupCreateOptions.create().mkstream(true));
                    } catch (RedisBusyException e) {
                        // Ignore BUSYGROUP Consumer Group name already exists
                    }

                    this.consumer = new RedisStreamConsumer(adapter,
                            RedisStreamConsumerProperties.create()
                                    .setGroup(this.group)
                                    .setName(this.id)
                                    .setMaxInFlight(this.maxInFlight)
                                    .setMaxPollingTimeout(this.maxPollingTimeout)
                                    .setClaimMinIdleTime(this.claimMinIdleTime)
                                    .setIdlingTimeout(this.idlingTimeout)
                                    .setClaimSensitivity(this.claimSensitivity)
                                    .setClaimOccurrenceRate(this.claimOccurrenceRate));
                }
            }
        }
    }

    @Override
    public void run(SourceContext ctx) {
        MessageHandler handler = new MessageHandler(ctx, this);
        try {
            consumer.subscribe(new RedisStreamOffset[]{
                    RedisStreamOffset.fromNeverDelivered(this.stream)
            }, handler, this.concurrency);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        log.info("redis source cancel.");
        this.close();
    }

    @SneakyThrows
    @Override
    public void close() {
        if (this.consumer != null) {
            log.info("kafka source close.");
            this.consumer.close();
            super.close();
        }
    }

    private ICommitAction createCommitAction(String commitAction) {
        switch (commitAction) {
            case COMMIT_ACTION_CODE:
                return new CommitAction();
            case DELETE_ACTION_CODE:
                return new DeleteAction();
        }
        throw new UnsupportedOperationException();
    }

    private void initialize(Properties props) {
        if (props.get("host") == null) {
            throw new IllegalArgumentException("specified argument 'host' must not be null.");
        }
        if (props.get("port") == null) {
            throw new IllegalArgumentException("specified argument 'port' must not be null.");
        }
        if (props.get("db") == null) {
            throw new IllegalArgumentException("specified argument 'db' must not be null.");
        }
        if (props.get("stream.group") == null) {
            throw new IllegalArgumentException("specified argument 'stream.group' must not be null.");
        }
        if (props.get("stream.name") == null) {
            throw new IllegalArgumentException("specified argument 'stream.name' must not be null.");
        }

        if (props.get("max.in.flight") != null) {
            this.maxInFlight = Integer.parseInt(props.get("max.in.flight").toString());
        }
        if (props.get("max.polling.timeout.ms") != null) {
            this.maxPollingTimeout = Duration.ofMillis(Long.parseLong(props.get("max.polling.timeout.ms").toString()));
        }
        if (props.get("claim.min.idle.time") != null) {
            this.claimMinIdleTime = Duration.ofSeconds(Long.parseLong(props.get("claim.min.idle.time").toString()));
        }
        if (props.get("idling.timeout.ms") != null) {
            this.idlingTimeout = Duration.ofMillis(Long.parseLong(props.get("idling.timeout.ms").toString()));
        }
        if (props.get("claim.sensitivity") != null) {
            this.claimSensitivity = Integer.parseInt(props.get("claim.sensitivity").toString());
        }
        if (props.get("claim.occurrence.rate") != null) {
            this.claimOccurrenceRate = Integer.parseInt(props.get("claim.occurrence.rate").toString());
        }
        if (props.get("auto.commit.action") != null) {
            this.commitAction = props.get("auto.commit.action").toString();
        }
        if (props.get("auto.commit.interval.ms") != null) {
            this.commitInterval = Duration.ofMillis(Long.parseLong(props.get("auto.commit.interval.ms").toString()));
        }
        if (props.get("concurrency") != null) {
            this.concurrency = Integer.parseInt(props.get("concurrency").toString());
        }
        if (this.claimMinIdleTime.compareTo(this.commitInterval) <= 0) {
            log.warn("auto commit will not work.");
        }
    }

    private class MessageHandler implements IRedisStreamMessageHandler {

        private SourceContext ctx;
        private String id;
        private String type;
        private String route;
        private Map<String, MappingField> mappingTable;
        private volatile transient ScheduledExecutorService scheduledThreadPool;
        private volatile LinkedList<RedisStreamMessage> queue = new LinkedList<>();

        public MessageHandler(SourceContext ctx, RedisSource parent) {
            this.ctx = ctx;
            this.id = parent.id;
            this.type = parent.type;
            this.route = parent.route;
            this.mappingTable = parent.mappingTable;
            createAutoCommitRunner(parent.commitAction, parent.commitInterval);
        }

        @Override
        public void process(RedisStreamMessage message) throws Exception {
            Map<String, String> body = message.getBody();
            if (body != null) {
                Map<String, Object> source = new HashMap<>(body);
                // 處理 json to RawContent
                this.mappingTable.forEach((k, v) -> {
                    if (v.getType().equals("json")) {
                        source.put(k, RawContent.builder()
                                .name(k)
                                .data(body.get(k))
                                .build());
                    }
                });
                try {
                    InputStreamContext data = InputStreamContext.builder()
                            .id(this.id)
                            .route(this.route)
                            .mappingTable(this.mappingTable)
                            .source(new MapValue(source))
                            .contentSourceType(this.type)
                            .rawContent(body)
                            .build();
                    this.queue.add(message);
                    ctx.collect(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void createAutoCommitRunner(String commitAction, Duration interval) {
            if (this.scheduledThreadPool == null) {
                ICommitAction action = createCommitAction(commitAction);
                synchronized (this) {
                    if (this.scheduledThreadPool == null) {
                        this.scheduledThreadPool = Executors.newScheduledThreadPool(5);
                        Runnable task = () -> {
                            RedisStreamMessage next = queue.peekFirst();
                            while (next != null) {
                                RedisStreamMessageId id = RedisStreamMessageId.parse(next.getId());
                                long timestamp = id.getId0();
                                if ((System.currentTimeMillis() - timestamp) >= interval.toMillis()) {
                                    action.commit(next);
                                    this.queue.removeFirst();
                                    next = this.queue.peekFirst();
                                } else {
                                    break;
                                }
                            }
                        };
                        scheduledThreadPool.scheduleAtFixedRate(task, interval.toSeconds(), interval.toSeconds(), TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    private interface ICommitAction {
        void commit(RedisStreamMessage m);
    }

    private class CommitAction implements ICommitAction {
        @Override
        public void commit(RedisStreamMessage m) {
            m.commit();
        }
    }

    private class DeleteAction implements ICommitAction {
        @Override
        public void commit(RedisStreamMessage m) {
            m.commitAndExpel();
        }
    }

}
