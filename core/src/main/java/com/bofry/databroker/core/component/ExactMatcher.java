package com.bofry.databroker.core.component;

// TODO 使用模糊比對，有任何的 * 就模糊比對，沒有就精準比對。
// 將 * 取代為 正則的 . 來做處理。
// round-*-sink-* to [round-.*-sink-.*]
public class ExactMatcher implements INameMatcher {

    private final Object _value;

    public ExactMatcher(Object _value) {
        this._value = _value;
    }

    @Override
    public void init() {
        if (_value == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean match(String srcRoute) {
        if (srcRoute == null) {
            return true;
        }
        return srcRoute.equals(this._value.toString());
    }

}
