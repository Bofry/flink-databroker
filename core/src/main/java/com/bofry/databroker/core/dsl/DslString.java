package com.bofry.databroker.core.dsl;

// TODO: implement serializable
public final class DslString extends DslValue {
    private final String value;

    public DslString(String value) {
        if (value == null)
            throw new IllegalArgumentException(String.format("specified argument cannot '%s' be null", "value"));

        this.value = decode(value);
    }

    @Override
    public DslValueType getType() {
        return DslValueType.String;
    }

    @Override
    public DslLiteral toLiteral() {
        StringBuffer buffer = new StringBuffer();

        buffer.append('"');
        for (int i = 0; i < this.value.length(); i++) {
            char c = this.value.charAt(i);
            switch (c) {
                case '\\':
                    buffer.append('\\'); buffer.append('\\');
                    break;
                case '"':
                    buffer.append('\\'); buffer.append('"');
                    break;
                case '/':
                    buffer.append('\\'); buffer.append('/');
                    break;
                case '\b':
                    buffer.append('\\'); buffer.append('b');
                    break;
                case '\f':
                    buffer.append('\\'); buffer.append('f');
                    break;
                case '\n':
                    buffer.append('\\'); buffer.append('n');
                    break;
                case '\r':
                    buffer.append('\\'); buffer.append('r');
                    break;
                case '\t':
                    buffer.append('\\'); buffer.append('t');
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        buffer.append('"');
        return new DslLiteral(buffer.toString());
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static String valueOf(DslString s) {
        return s.value;
    }

    private String decode(String input) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\\') {
                char next = input.charAt(i + 1);
                switch (next) {
                    case '\\':
                        buffer.append('\\');
                        break;
                    case '"':
                        buffer.append('"');
                        break;
                    case '/':
                        buffer.append('/');
                        break;
                    case 'b':
                        buffer.append('\b');
                        break;
                    case 'f':
                        buffer.append('\f');
                        break;
                    case 'n':
                        buffer.append('\n');
                        break;
                    case 'r':
                        buffer.append('\r');
                        break;
                    case 't':
                        buffer.append('\t');
                        break;
                    default:
                        buffer.append(c);
                        buffer.append(next);
                        break;
                    }
                    i++;
            } else {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }
}
