package com.bofry.databroker.core.redis.stream;

import io.lettuce.core.*;
import io.lettuce.core.XReadArgs.StreamOffset;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.models.stream.PendingMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LettuceStreamHostConnection extends LettuceStreamConnection {
    private final RedisClient client;
    private StreamClient<String, String> streamClient;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public LettuceStreamHostConnection(RedisClient client) {
        if (client == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "client"));

        this.client = client;
    }

    @Override
    protected LettuceStreamClient<String, String> getStreamClient() {
        if (this.closed.get())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_Closed, this.getClass()));
        if (!this.connected.get())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_NotReady, this.getClass()));

        return this.streamClient;
    }

    @Override
    public void connect() {
        if (this.closed.get())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_Closed, this.getClass()));
        if (this.connected.get())
            return;

        synchronized (this) {
            if (this.connected.compareAndSet(false, true)) {
                try {
                    RedisCommands<String, String> worker = this.client.connect(StringCodec.UTF8).sync();
                    this.streamClient = new StreamClient<>(worker);
                } catch (Exception ex) {
                    this.connected.set(false);
                    this.close();
                }
            }
        }
    }

    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.client.shutdown();
        }
    }

    @Override
    protected boolean isConnected() {
        return this.connected.get();
    }

    @Override
    protected boolean isClosed() {
        return this.closed.get();
    }

    static class StreamClient<K, V> extends LettuceStreamClient<K, V> {
        private static final String INFO_KEY_REDIS_VERSION = "redis_version:";

        private final RedisCommands<K, V> worker;

        StreamClient(RedisCommands<K, V> client) {
            this.worker = client;
        }

        @Override
        protected Long xack(K key, K group, String... messageIds) {
            return this.worker.xack(key, group, messageIds);
        }

        @Override
        protected String xadd(K key, XAddArgs args, Map<K, V> body) {
            return this.worker.xadd(key, args, body);
        }

        @Override
        protected List<StreamMessage<K, V>> xclaim(K key, Consumer<K> consumer, XClaimArgs args, String... messageIds) {
            return this.worker.xclaim(key, consumer, args, messageIds);
        }

        @Override
        protected Long xdel(K key, String... messageIds) {
            return this.worker.xdel(key, messageIds);
        }

        @Override
        protected List<PendingMessage> xpending(K key, K group, Range<String> range, Limit limit) {
            return this.worker.xpending(key, group, range, limit);
        }

        @Override
        protected List<StreamMessage<K, V>> xreadgroup(Consumer<K> consumer, XReadArgs args, StreamOffset<K>[] streams) {
            return this.worker.xreadgroup(consumer, args, streams);
        }

        @Override
        protected String xgroupCreate(XReadArgs.StreamOffset<K> streamOffset, K group, XGroupCreateArgs args) {
            return this.worker.xgroupCreate(streamOffset, group, args);
        }

        @Override
        protected Boolean xgroupDestroy(K key, K group) {
            return this.worker.xgroupDestroy(key, group);
        }

        @Override
        protected String xgroupSetid(XReadArgs.StreamOffset<K> streamOffset, K group) {
            return this.worker.xgroupSetid(streamOffset, group);
        }

        @Override
        protected Long xgroupDelconsumer(K key, Consumer<K> consumer) {
            return this.worker.xgroupDelconsumer(key, consumer);
        }

        @Override
        protected List<StreamMessage<K, V>> xrange(K key, Range<String> range) {
            return this.worker.xrange(key, range);
        }

        @Override
        protected boolean isSupported(RedisStreamVersion version) {
            String serverVer = this.parseRedisServerVersion();
            if (serverVer != null) {
                int comparison = version.compareTo(serverVer);
                return comparison <= 0;
            }
            return false;
        }

        private String parseRedisServerVersion() {
            String version = null;

            try {
                String reply = this.worker.info("SERVER");
                BufferedReader reader = new BufferedReader(new StringReader(reply));

                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.startsWith(INFO_KEY_REDIS_VERSION)) {
                        version = line.substring(INFO_KEY_REDIS_VERSION.length()).trim();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return version;
        }
    }
}
