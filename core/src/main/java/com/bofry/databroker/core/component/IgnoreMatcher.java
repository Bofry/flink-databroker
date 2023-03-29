package com.bofry.databroker.core.component;

public class IgnoreMatcher implements INameMatcher {

    @Override
    public void init() {
    }

    @Override
    public boolean match(String srcRoute) {
        return true;
    }
}
