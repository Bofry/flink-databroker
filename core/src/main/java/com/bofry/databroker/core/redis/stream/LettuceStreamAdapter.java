package com.bofry.databroker.core.redis.stream;

import io.lettuce.core.*;
import io.lettuce.core.models.stream.PendingMessage;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LettuceStreamAdapter extends RedisStreamAdapter {
    private static final int MAX_CACHE_ENTRIES = 32;
    private static final Range<String> FULL_RANGE = Range.create("-", "+");

    private final LettuceStreamClient<String, String> client;

    private final transient Map<Object, Object> cache = createCacheMap();

    public LettuceStreamAdapter(LettuceStreamConnection connection) {
        super(connection);

        LettuceStreamClient<String, String> client = connection.getStreamClient();
        if (!client.isSupported(RedisStreamVersion.RedisV5))
            throw new RuntimeException(SR.getString(SR.RedisStreamAdapter_UnsupportedRedisVersion, connection.getClass()));

        this.client = connection.getStreamClient();
    }

    @Override
    public String xadd(String stream, String id, Map<String, String> content) {
        XAddArgs args;
        {
            args = new XAddArgs();
            args.id(id);
        }

        return this.client.xadd(stream, args, content);
    }

    // NOTE: On Redis 5.0.9, if arguemnt 'limit' is a 0 or negative integer, the Redis will reply an empty result.
    @Override
    public Iterator<RedisStreamMessage> xclaim(String stream, String group, String consumer, Duration minIdleTime,
                                               int limit, int pendingFetchingSize) {
        if (Assertion.isNullOrEmpty(stream))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "stream"));
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));
        if (Assertion.isNullOrEmpty(consumer))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "consumer"));

        List<String> unhandledMessagesIds = new ArrayList<>(limit);
        List<PendingMessage> unhandledMessages = this.client.xpending(stream, group, FULL_RANGE, Limit.from(pendingFetchingSize));

        for (int i = 0; i < unhandledMessages.size(); i++) {
            PendingMessage item = unhandledMessages.get(i);

            if (item.getSinceLastDelivery().compareTo(minIdleTime) >= 0) {
                unhandledMessagesIds.add(item.getId());

                if (unhandledMessagesIds.size() >= limit) {
                    break;
                }
            }
        }

        if (unhandledMessagesIds.size() > 0) {
            Consumer<String> lettuceConsumer = Consumer.from(group, consumer);
            XClaimArgs args = XClaimArgs.Builder.minIdleTime(minIdleTime);

            String[] unhandleIds = unhandledMessagesIds.toArray(String[]::new);

            List<StreamMessage<String, String>> messages = this.client.xclaim(stream, lettuceConsumer, args, unhandleIds);
            if (messages.size() > 0) {
                StreamContext context = new StreamContext(this, group, consumer);
                return new ClaimStreamMessageIterator(context, unhandleIds, messages.iterator());
            }
        }

        return Collections.emptyIterator();
    }

    // NOTE: On Redis 5.0.9, if arguemnt 'limit' is a 0 or negative integer, the Redis will using default value 10 as size.
    @Override
    public Iterator<RedisStreamMessage> xreadgroup(String group, String consumer, RedisStreamOffset[] offsets, Duration timeout, int limit) {
        if (Assertion.isNullOrEmpty(group))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "group"));
        if (Assertion.isNullOrEmpty(consumer))
            throw new IllegalArgumentException(SR.getString(SR.ArgumentNullOrEmptyString, "consumer"));

        if (offsets.length > 0) {
            Consumer<String> lettuceConsumer = Consumer.from(group, consumer);
            XReadArgs args = XReadArgs.Builder.block(timeout).count(limit);

            XReadArgs.StreamOffset<String>[] lettuceOffsets = getCachedStreamOffset(offsets);

            List<StreamMessage<String, String>> messages = this.client.xreadgroup(lettuceConsumer, args, lettuceOffsets);
            if (messages.size() > 0) {
                StreamContext context = new StreamContext(this, group, consumer);
                return new StreamMessageIterator(context, messages.iterator());
            }
        }

        return Collections.emptyIterator();
    }

    @Override
    public long xack(String stream, String group, String... messageIds) {
        return this.client.xack(stream, group, messageIds);
    }

    @Override
    public long xdel(String stream, String... messageIds) {
        return this.client.xdel(stream, messageIds);
    }

    @Override
    public String xgroupCreate(RedisStreamOffset offset, String group, RedisStreamGroupCreateOptions opts) {
        XReadArgs.StreamOffset<String> streamOffset = XReadArgs.StreamOffset.from(offset.getStream(), offset.getOffset());

        XGroupCreateArgs args = new XGroupCreateArgs();
        {
            args.mkstream(opts.mkstream);
        }
        return this.client.xgroupCreate(streamOffset, group, args);
    }

    @Override
    public boolean xgroupDestory(String stream, String group) {
        return this.client.xgroupDestroy(stream, group);
    }

    @Override
    public String xgroupSetId(RedisStreamOffset offset, String group) {
        XReadArgs.StreamOffset<String> streamOffset = XReadArgs.StreamOffset.from(offset.getStream(), offset.getOffset());

        return this.client.xgroupSetid(streamOffset, group);
    }

    @Override
    public long xgroupDelConsumer(String stream, String group, String consumer) {
        Consumer<String> lettuceConsumer = Consumer.from(group, consumer);

        return this.client.xgroupDelconsumer(stream, lettuceConsumer);
    }

    @Override
    public Iterator<RedisStreamMessage> xrange(String stream, String startId, String endId) {
        Range<String> range = Range.create(startId, endId);
        List<StreamMessage<String, String>> messages = this.client.xrange(stream, range);
        if (messages.size() > 0) {
            StreamContext context = new StreamContext(this);
            return new StreamMessageIterator(context, messages.iterator());
        }
        return Collections.emptyIterator();
    }

    @SuppressWarnings("unchecked")
    private XReadArgs.StreamOffset<String>[] getCachedStreamOffset(RedisStreamOffset[] offsets) {
        Object value = this.cache.get(offsets);
        if (value != null) {
            return (XReadArgs.StreamOffset<String>[]) value;
        }

        XReadArgs.StreamOffset<String>[] lettuceOffsets;
        {
            List<XReadArgs.StreamOffset<String>> lettuceOffsetInfos = new ArrayList<>(offsets.length);
            for (int i = 0; i < offsets.length; i++) {
                RedisStreamOffset info = offsets[i];
                XReadArgs.StreamOffset<String> lettuceOffsetInfo = XReadArgs.StreamOffset.from(info.getStream(), info.getOffset());
                lettuceOffsetInfos.add(lettuceOffsetInfo);
            }
            lettuceOffsets = lettuceOffsetInfos.toArray(XReadArgs.StreamOffset[]::new);
            this.cache.put(offsets, lettuceOffsets);
        }

        return lettuceOffsets;
    }

    static Map<Object, Object> createCacheMap() {
        return new LinkedHashMap<Object, Object>(MAX_CACHE_ENTRIES + 1, 1.0F, true) {
            protected boolean removeEldestEntry(Map.Entry<Object, Object> entry) {
                return (this.size() > MAX_CACHE_ENTRIES);
            }

            ;
        };
    }

    static class StreamContext {
        private final RedisStreamAdapter adapter;
        private final String group;
        private final String consumer;

        StreamContext(RedisStreamAdapter adapter) {
            this(adapter, null, null);
        }

        StreamContext(RedisStreamAdapter adapter, String group, String consumer) {
            this.adapter = adapter;
            this.group = group;       // might be null
            this.consumer = consumer; // might be null
        }

        public boolean hasMessage(String stream, String id) {
            Iterator<RedisStreamMessage> messages = this.adapter.xrange(stream, id, id);
            return messages.hasNext();
        }

        public long commit(String stream, String... messageIds) {
            if (this.group != null) {
                return this.adapter.xack(stream, this.group, messageIds);
            }
            return 0;
        }

        public long expel(String stream, String... messageIds) {
            return this.adapter.xdel(stream, messageIds);
        }
    }

    static class StreamMessageIterator implements Iterator<RedisStreamMessage> {
        private final StreamContext context;
        private final Iterator<StreamMessage<String, String>> inner;

        StreamMessageIterator(
                StreamContext context,
                Iterator<StreamMessage<String, String>> iterator) {

            this.context = context;
            this.inner = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.inner.hasNext();
        }

        @Override
        public RedisStreamMessage next() {
            StreamMessage<String, String> message = this.inner.next();
            return new StreamMessageContent(context, message);
        }
    }

    static class StreamMessageContent extends RedisStreamMessage {
        private final StreamContext context;
        private final StreamMessage<String, String> message;

        StreamMessageContent(StreamContext context, StreamMessage<String, String> message) {
            this.context = context;
            this.message = message;
        }

        @Override
        public String getStream() {
            return this.message.getStream();
        }

        @Override
        public String getId() {
            return this.message.getId();
        }

        @Override
        public Map<String, String> getBody() {
            return this.message.getBody();
        }

        @Override
        public boolean isValid() {
            return this.message.getId() != null;
        }

        @Override
        public boolean commit() {
            if (isValid()) {
                long reply = this.context.commit(getStream(), getId());
                return 1 == reply;
            }
            return false;
        }

        @Override
        public boolean expel() {
            if (isValid()) {
                long reply = this.context.expel(getStream(), getId());
                return 1 == reply;
            }
            return false;
        }
    }

    static class ClaimStreamMessageIterator extends StreamMessageIterator {
        private final String[] ghostIds;
        private final AtomicInteger nextGhostIdIndex = new AtomicInteger(0);

        ClaimStreamMessageIterator(
                StreamContext context,
                String[] ghostIds,
                Iterator<StreamMessage<String, String>> iterator) {

            super(context, iterator);
            this.ghostIds = ghostIds;
        }

        @Override
        public RedisStreamMessage next() {
            StreamMessage<String, String> message = super.inner.next();

            if (this.nextGhostIdIndex.get() < this.ghostIds.length) {
                String id = this.ghostIds[this.nextGhostIdIndex.getAndIncrement()];
                return new ClaimStreamMessageContent(super.context, id, message);
            }
            throw new NoSuchElementException();
        }
    }

    static class ClaimStreamMessageContent extends StreamMessageContent {
        private final String ghostId;

        ClaimStreamMessageContent(StreamContext context, String ghostId, StreamMessage<String, String> message) {
            super(context, message);
            this.ghostId = ghostId;
        }

        @Override
        public boolean commit() {
            if (isValid()) {
                return super.commit();
            }

            if (this.ghostId != null) {
                // double check id
                if (false == super.context.hasMessage(getStream(), ghostId)) {
                    long reply = super.context.commit(getStream(), ghostId);
                    return 1 == reply;
                }
            }
            return false;
        }

    }

}
