package com.bofry.databroker.core.redis.stream;

public enum RedisStreamVersion {
    RedisV5(5000), RedisV6_2(6002);

    private final int value;

    private RedisStreamVersion(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        int major = this.value / 1000;
        int minor = this.value % 1000;
        return String.format("%d.%d", major, minor);
    }

    public int compareTo(String version) {
        if (version == null)
            throw new IllegalArgumentException(SR.getString(SR.ArgumentMustNotNull, "version"));

        String[] parts = version.split("\\.");

        int major = 0;
        int minor = 0;

        try {
            if (parts.length == 1) {
                major = Integer.valueOf(parts[0]);
            } else if (parts.length > 1) {
                major = Integer.valueOf(parts[0]);
                minor = Integer.valueOf(parts[1]);
            }
        } catch (NumberFormatException ex) {
            throw new RuntimeException(SR.getString(SR.RedisStreamVersion_InvalidFormat, "version"));
        }

        int another;
        {
            if ((major < 0) || (minor < 0) || (minor > 999))
                throw new RuntimeException(SR.getString(SR.RedisStreamVersion_InvalidFormat, "version"));

            another = major * 1000 + minor;
        }
        return Integer.compare(this.value, another);
    }
}
