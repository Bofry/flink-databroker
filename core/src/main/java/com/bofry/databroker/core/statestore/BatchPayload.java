package com.bofry.databroker.core.statestore;

import java.util.ArrayList;
import java.util.List;

public final class BatchPayload<E> {
    private final IPayloadFormatter _formatter;
    private final List<String> _container = new ArrayList<>();

    BatchPayload(IPayloadFormatter formatter) {
        _formatter = formatter;
    }

    public boolean isEmpty() {
        return _container.size() == 0;
    }

    public long recordCount() {
        return _container.size();
    }

    public void write(E entity) {
        String content = _formatter.marshal(entity);
        _container.add(content);
    }

    public String readAsString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < _container.size(); i++) {
            builder.append(_container.get(i));
        }
        return builder.toString();
    }

    void writeContent(String content) {
        _container.add(content);
    }
}
