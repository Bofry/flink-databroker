package com.bofry.databroker.core.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StateStoreManager {

    private final Map<Object, Object> container = new HashMap<>();
    private static final StateStoreManager instance = new StateStoreManager();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    public static Object get(Object k) {
        return instance.container.get(k);
    }

    public static void set(Object k, Object v) {
        if (v == null) {
            instance.container.remove(k);
            return;
        }
        instance.container.put(k, v);
    }

    public static void remove(Object k) {
        set(k, null);
    }

    public static void lock() {
        instance.reentrantReadWriteLock.writeLock().lock();
    }

    public static void unlock() {
        instance.reentrantReadWriteLock.writeLock().unlock();
    }

    public static StateStoreManager getInstance() {
        return instance;
    }

    public Map<Object, Object> getContainer() {
        return this.container;
    }

}
