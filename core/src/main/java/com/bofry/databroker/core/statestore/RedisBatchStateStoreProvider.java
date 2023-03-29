package com.bofry.databroker.core.statestore;

import java.util.List;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisBatchStateStoreProvider {
    private RedisClient _redis;

    public void info(int db, String namespace) {

    }

    public void push(int db, String namespace, long length, String record) {

    }

    public void pop(int db, String namespace, long idempotence) {

    }

    public void pop(int db, String namespace) {

    }

    private void getScript(int db, String namespace, String name) {
        /*
            SELECT <db>
            HGET <script> name
            SCRIPT EXISTS <sha1>
        */
        RedisCommands<String, String> session = _redis.connect().sync();
        session.select(db);
        String scriptId = session.hget("db:" + namespace + "/scripts", name);
        List<Boolean> result = session.scriptExists(scriptId);
        if (result.get(0).equals(Boolean.FALSE)) {

        }
    }

    private void executeScript(String name, String[] keys, String ...args) {
        /*
        script  :  <namespace>/scripts
        metadata:  <namespace>/batchrecord/meta
        record  :  <namespace>/batchrecord/data

        info <db> <namespace>
            SELECT <db>
            HMGET <metadata> "idempotence" "payload_size"
            LLEN  <record>

        push <db> <namespace> <size> <record>
            SEELCT <db>
            RPUSH  <record> <record>
            HINCRBY <metadata> "idempotence" 1
            HINCRBY <metadata> "payload_size" <size>

        pop <db> <namespace> <idempotence>
            SEELCT <db>
            HGET <metadata> "idempotence"

            if  IDEMPOTENCE = final_idempotence  then
                LRANGE  <record> 0 -1
                HINCRBY <metadata> "idempotence" 1
                HDEL    <metadata> "payload_size"
            end
        */
        String script = "";
    }
}
