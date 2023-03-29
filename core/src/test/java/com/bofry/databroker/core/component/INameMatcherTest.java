package com.bofry.databroker.core.component;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class INameMatcherTest {

    @Test
    public void regexMatcherMatchTest() {
        RegexMatcher matcher = new RegexMatcher(Pattern.compile("^[a-zA-Z0-9._.-]+"));
        matcher.init();
        Assert.assertTrue(matcher.match("round-sink"));
    }

    @Test
    public void exactMatcherMatchTest() {
        ExactMatcher matcher = new ExactMatcher("round-sink");
        matcher.init();
        Assert.assertTrue(matcher.match("round-sink"));
    }

    @Test
    public void regexMatcherWithoutSrcRouteTest() {
        RegexMatcher matcher = new RegexMatcher(Pattern.compile("^[a-zA-Z0-9]+"));
        matcher.init();
        Assert.assertTrue(matcher.match(null));
    }

    @Test
    public void exactMatcherWithoutSrcRouteTest() {
        ExactMatcher matcher = new ExactMatcher("round-sink");
        matcher.init();
        Assert.assertTrue(matcher.match(null));
    }

    @Test
    public void regexMatcherNotMatchTest() {
        RegexMatcher matcher = new RegexMatcher(Pattern.compile("^[a-zA-Z0-9]+"));
        matcher.init();
        Assert.assertFalse(matcher.match("round-sink"));
    }

    @Test
    public void exactMatcherNotMatchTest() {
        ExactMatcher matcher = new ExactMatcher("round-sink!");
        matcher.init();
        Assert.assertFalse(matcher.match("round-sink"));
    }

    @Test
    public void ignoreMatcherMatchTest() {
        IgnoreMatcher matcher = new IgnoreMatcher();
        matcher.init();
        Assert.assertTrue(matcher.match(null));
    }

}
