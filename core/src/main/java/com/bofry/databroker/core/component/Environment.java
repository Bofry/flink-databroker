package com.bofry.databroker.core.component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Environment {

    private static final String ENV_PATTERN = "\\$\\{([A-Za-z0-9_]+)}";
    private static final Pattern REGEX = Pattern.compile(ENV_PATTERN);

    private static final Environment instance = new Environment();
    private final CascadeConfigProvider container = new CascadeConfigProvider();

    public Environment() {
        this.container.addConfigProvider(new EnvironmentVariableProvider());
    }

    public String get(String key) {
        return this.container.get(key);
    }

    public void _add(IConfigProvider p) {
        this.container.addConfigProvider(p);
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void load(Stream stream) {
        // TODO
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void load(String filePath) {
        // TODO
    }

    public String expand(String input) {
        String output = input;
        Matcher matcher = REGEX.matcher(input);
        while (matcher.find()) {
            String envValue = this.container.get(matcher.group(1));
            envValue = envValue.replace("\\", "\\\\");
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            output = subexpr.matcher(output).replaceAll(envValue);
        }
        return output;
    }

    public static Environment getInstance() {
        return instance;
    }

}
