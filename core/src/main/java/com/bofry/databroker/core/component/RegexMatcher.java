package com.bofry.databroker.core.component;

import java.util.regex.Pattern;

public class RegexMatcher implements INameMatcher {

    private Pattern pattern;

    @Override
    public void init() {
        if (pattern == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean match(String srcRoute) {
        if (srcRoute == null) {
            return true;
        }
        return this.pattern.matcher(srcRoute).matches();
    }

    public Pattern getPattern() {
        return pattern;
    }

    public RegexMatcher() {
    }

    public RegexMatcher(Pattern pattern) {
        this.pattern = pattern;
    }
}
