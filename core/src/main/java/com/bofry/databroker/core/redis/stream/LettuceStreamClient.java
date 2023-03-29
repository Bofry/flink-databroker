package com.bofry.databroker.core.redis.stream;

import io.lettuce.core.*;
import io.lettuce.core.models.stream.PendingMessage;

import java.util.List;
import java.util.Map;

public abstract class LettuceStreamClient<K, V> {
    protected abstract Long xack(K key, K group, String... messageIds);

    protected abstract String xadd(K key, XAddArgs args, Map<K, V> body);

    protected abstract List<StreamMessage<K, V>> xclaim(K key, Consumer<K> consumer, XClaimArgs args, String... messageIds);

    protected abstract Long xdel(K key, String... messageIds);

    protected abstract List<PendingMessage> xpending(K key, K group, Range<String> range, Limit limit);

    protected abstract List<StreamMessage<K, V>> xreadgroup(Consumer<K> consumer, XReadArgs args, XReadArgs.StreamOffset<K>[] streams);

    protected abstract String xgroupCreate(XReadArgs.StreamOffset<K> streamOffset, K group, XGroupCreateArgs args);

    protected abstract Boolean xgroupDestroy(K key, K group);

    protected abstract String xgroupSetid(XReadArgs.StreamOffset<K> streamOffset, K group);

    protected abstract Long xgroupDelconsumer(K key, Consumer<K> consumer);

    protected abstract List<StreamMessage<K, V>> xrange(K key, Range<String> range);

    protected abstract boolean isSupported(RedisStreamVersion version);
}
