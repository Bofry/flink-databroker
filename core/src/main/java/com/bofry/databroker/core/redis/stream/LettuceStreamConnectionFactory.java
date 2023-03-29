package com.bofry.databroker.core.redis.stream;

import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;

public final class LettuceStreamConnectionFactory {

    public static LettuceStreamConnection create(RedisClient client) {
        return new LettuceStreamHostConnection(client);
    }

    @Deprecated
    public static LettuceStreamConnection create(RedisClusterClient client) {
        // FIXME: not implemented LettuceStreamClusterConnection
        throw new UnsupportedOperationException();
    }
}
