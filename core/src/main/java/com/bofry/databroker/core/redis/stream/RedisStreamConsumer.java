package com.bofry.databroker.core.redis.stream;

import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class RedisStreamConsumer {
    private static final int MAX_PENDING_FETCHING_SIZE = 512;
    private static final int MIN_PENDING_FETCHING_SIZE = 16;
    private static final int PENDING_FETCHING_SIZE_COEFFICIENT = 3;

    private final RedisStreamAdapter adapter;
    private final RedisStreamConnection connection;

    private final String group;
    private final String name;
    private final int maxInFlight;
    private final Duration maxPollingTimeout;
    private final Duration claimMinIdleTime;
    private final Duration idlingTimeout; // 若沒有任何訊息時等待多久
    private final int claimSensitivity; // Read 時取得的訊息數小於 n 的話, 執行 Claim
    private final int claimOccurrenceRate; // Read 每執行 n 次後 執行 Claim 1 次

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public RedisStreamConsumer(RedisStreamAdapter adapter, RedisStreamConsumerProperties prop) {
        if (adapter == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "adapter"));
        if (prop == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "prop"));

        {
            prop.initialize();
            this.group = prop.group;
            this.name = prop.name;
            this.maxInFlight = prop.maxInFlight;
            this.maxPollingTimeout = prop.maxPollingTimeout;
            this.claimMinIdleTime = prop.claimMinIdleTime;
            this.idlingTimeout = prop.idlingTimeout;
            this.claimSensitivity = prop.claimSensitivity;
            this.claimOccurrenceRate = prop.claimOccurrenceRate;
        }

        this.adapter = adapter;
        this.connection = adapter.getConnection();
    }

    public void subscribe(RedisStreamOffset[] streams, IRedisStreamMessageHandler handler) throws Exception {
        subscribe(streams, handler, 1);
    }

    public void subscribe(RedisStreamOffset[] streams, IRedisStreamMessageHandler handler, int concurrency) throws Exception {
        if (streams == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "streams"));
        if (handler == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "handler"));

        if (this.closed.get())
            throw new RuntimeException(SR.getString(SR.RedisStreamConsumer_Closed, this.getClass()));
        if (this.connection.isClosed())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_Closed, this.connection.getClass()));
        if (!this.connection.isConnected())
            throw new RuntimeException(SR.getString(SR.RedisStreamConnection_NotReady, this.connection.getClass()));

        // TODO validate concurrency must positive
        if (concurrency < 0)
            concurrency = 1;

        CyclicCounter claimTrigger = new CyclicCounter(this.claimOccurrenceRate);
        MessageHandler messageHandler = new MessageHandler(handler, concurrency);

        try {
            while (!this.closed.get() && !this.connection.isClosed()) {
                int readMessages = poll(messageHandler, streams);

                // should XCLAIM ?
                if (claimTrigger.spin() || readMessages < this.claimSensitivity) {
                    int pendingFetchingSize = computePendingFetchingSize(this.maxInFlight);
                    for (int i = 0; i < streams.length; i++) {
                        String stream = streams[i].getStream();
                        Iterator<RedisStreamMessage> messages = this.adapter.xclaim(stream, this.group, this.name,
                                this.claimMinIdleTime, this.maxInFlight, pendingFetchingSize);

                        while (messages.hasNext()) {
                            RedisStreamMessage m = messages.next();
                            if (m.isValid()) {
                                messageHandler.process(m);
                            } else {
                                m.commit();
                            }
                        }
                    }
                }

                // FIXME swap check
                synchronized (this) {
                    if (readMessages == 0) {
                        try {
                            wait(this.idlingTimeout.toMillis());
                        } catch (InterruptedException ex) {
                            throw ex;
                        }
                    }
                }
            }
        } finally {
            messageHandler.close();
        }
    }

    public void close() {
        closed.compareAndSet(false, true);
    }

    private int poll(IRedisStreamMessageHandler handler, RedisStreamOffset[] streams) throws Exception {
        int readMessages = 0;

        Iterator<RedisStreamMessage> messages = this.adapter.xreadgroup(this.group, this.name, streams,
                this.maxPollingTimeout, this.maxInFlight);
        while (messages.hasNext()) {
            RedisStreamMessage m = messages.next();
            readMessages++;

            handler.process(m);
        }

        return readMessages;
    }

    private int computePendingFetchingSize(int maxInFlight) {
        int fetchingSize = maxInFlight * PENDING_FETCHING_SIZE_COEFFICIENT;
        if (fetchingSize < MIN_PENDING_FETCHING_SIZE) {
            return MIN_PENDING_FETCHING_SIZE;
        }
        if (fetchingSize > MAX_PENDING_FETCHING_SIZE) {
            return MAX_PENDING_FETCHING_SIZE;
        }
        return fetchingSize;
    }

    static class CyclicCounter {
        private final int max;
        private final AtomicInteger value = new AtomicInteger(0);

        CyclicCounter(int max) {
            if (max < 0)
                throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNonNegativeInteger, "max"));

            this.max = max;
        }

        public boolean spin() {
            if (this.max == 0)
                return false;

            this.value.incrementAndGet();
            return this.value.compareAndSet(this.max, 0);
        }

        public void reset() {
            this.value.set(0);
        }
    }

    static class MessageHandler implements IRedisStreamMessageHandler {
        private final IRedisStreamMessageHandler handler;
        private final Semaphore semaphore;
        private final int concurrency;

        MessageHandler(IRedisStreamMessageHandler handler, int concurrency) {
            this.handler = handler;
            this.concurrency = concurrency;
            this.semaphore = new Semaphore(this.concurrency);
        }

        @Override
        public void process(RedisStreamMessage message) throws Exception {
            try {
                this.semaphore.acquire();
                this.handler.process(message);
            } catch (InterruptedException e) {
                // FIXME throw
                throw e;
            } finally {
                this.semaphore.release();
            }
        }

        void close() throws Exception {
            try {
                this.semaphore.acquire(this.concurrency);
            } catch (InterruptedException e) {
                throw e;
            }
        }
    }

}
