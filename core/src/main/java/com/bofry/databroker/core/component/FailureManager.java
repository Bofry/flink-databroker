package com.bofry.databroker.core.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FailureManager implements IFailureHandler {

    public final static int DEFAULT_PRIORITY = 0;
    public final static int FAILURE_PRIORITY = 100;
    public final static int RETRYABLE_PRIORITY = 500;

    private final List<Item> items = new ArrayList<>();

    public void addHandler(int priority, IFailureHandler h) {
        if (h == null) {
            return;
        }
        Item i = new Item(priority, h);
        int index = Collections.binarySearch(items, i);
        if (index < 0) {
            items.add(~index, i);
        }
        // FIXME check again, why index < 0 ?
//        else {
//            throw new IllegalArgumentException();
//        }
    }

    @Override
    public boolean process(Exception e) {
        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if (item.handler.process(e)) {
                return true;
            }
        }
        return false;
    }

    class Item implements Comparable<Item> {

        private final int priority;
        private final IFailureHandler handler;

        public Item(int priority, IFailureHandler handler) {
            this.priority = priority;
            this.handler = handler;
        }

        @Override
        public int compareTo(Item o) {
            return Integer.compare(priority, o.priority);
        }
    }

}
