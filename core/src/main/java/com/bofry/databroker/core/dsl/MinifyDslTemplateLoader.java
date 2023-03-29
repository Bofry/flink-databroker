package com.bofry.databroker.core.dsl;

import ch.simschla.minify.js.JsMin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MinifyDslTemplateLoader {
    private static final int EOF = -1;
    private static final char[] TEXT_BLOCK_DELIMITER = DslTemplate.TEXT_BLOCK_DELIMITER;
    private static final char[] OPEN_PLACEHOLDER_DELIMITER = DslTemplate.OPEN_PLACEHOLDER_DELIMITER;
    private static final char[] CLOSE_PLACEHOLDER_DELIMITER = DslTemplate.CLOSE_PLACEHOLDER_DELIMITER;

    private final DslTemplate template = new DslTemplate();

    MinifyDslTemplateLoader() {
    }

    public static DslTemplate load(String input) throws IOException {
        MinifyDslTemplateLoader builder = new MinifyDslTemplateLoader();

        TemplateWriter writer = new TemplateWriter(builder.template);

        builder.minify(writer, input);

        return builder.template;
    }

    private void fillPlaceholder(TemplateWriter out, StringReader in, char[] openDelimiter, char[] closeDelimiter) throws IOException {
        StringBuffer content = new StringBuffer();

        int b;
        while (true) {
            in.mark(0);
            b = in.read();
            if (b == EOF)
                throw new RuntimeException(String.format("missing close delimiter '%s'", closeDelimiter));

            if (b <= 0x7F) {
                char c = (char) b;
                if ((c == closeDelimiter[0]) && (detectDelimiter(in, closeDelimiter))) {

                    out.openPlaceholderFragment(openDelimiter);
                    out.write(openDelimiter);
                    out.write(content.toString());
                    out.write(closeDelimiter);
                    out.closePlaceholderFragment(closeDelimiter);

                    return ;
                } else {
                    fill(content, in);
                }
            } else {
                fill(content, in);
            }
        }
    }

    private void minify(TemplateWriter out, String input) throws IOException {
        StringReader in = new StringReader(input);

        int chr;
        out.openLiteralFragment();
        while (true) {
            in.mark(0);
            chr = in.read();
            if (chr == EOF) break;

            if (chr <= 0x7F) {
                char c = (char)chr;
                switch (c) {
                    case '"':
                        if (detectDelimiter(in, TEXT_BLOCK_DELIMITER)){
                            fillTextBlockContent(out, in, TEXT_BLOCK_DELIMITER);
                        } else {
                            fillText(out, in, c);
                        }
                        break;
                    case '\r':
                    case '\n':
                    case '\t':
                    case ' ':
                        // ignore
                        break;
                    case '\\':
                        fill(out, in, 2);
                        break;
                    default:
                        if (detectDelimiter(in, OPEN_PLACEHOLDER_DELIMITER)) {
                            fillPlaceholder(out, in, OPEN_PLACEHOLDER_DELIMITER, CLOSE_PLACEHOLDER_DELIMITER);
                        } else {
                            fill(out, in);
                        }
                        break;
                }
            } else {
                fill(out, in);
            }
        }
        out.closeLiteralFragment();
    }

    private void fill(TemplateWriter out, StringReader in) throws IOException {
        in.reset();
        int b = in.read();
        if (b != EOF) {
            out.write(b);
        }
    }

    private void fill(TemplateWriter out, StringReader in, int size) throws IOException {
        if (size > 0) {
            in.reset();

            for (int i = 0; i < size; i++) {
                int b = in.read();
                if (b != EOF) {
                    out.write(b);
                }
            }
        }
    }

    private void fill(StringBuffer out, StringReader in) throws IOException {
        in.reset();
        int b = in.read();
        if (b != EOF) {
            out.append((char)b);
        }
    }

    private void fillText(TemplateWriter out, StringReader in, char delimiter) throws IOException {
        out.openTextFragment(delimiter);
        fill(out, in);

        int chr;
        while (true) {
            in.mark(0);
            chr = in.read();
            if (chr == EOF)
                throw new RuntimeException(String.format("missing close delimiter '%c'", delimiter));

            if (chr <= 0x7F) {
                char c = (char) chr;
                if (c == delimiter) {
                    fill(out, in);
                    out.closeTextFragment(delimiter);
                    return;
                } else {
                    switch (c) {
                        case '\\':
                            fill(out, in, 2);
                            break;
                        default:
                            fill(out, in);
                            break;
                    }
                }
            }
            else {
                fill(out, in);
            }
        }
    }

    private void fillTextBlockContent(TemplateWriter out, StringReader in, char[] delimiter) throws IOException {
        StringBuffer content = new StringBuffer();

        int b;
        while (true) {
            in.mark(0);
            b = in.read();
            if (b == EOF)
                throw new RuntimeException(String.format("missing close delimiter '%s'", delimiter));

            if (b <= 0x7F) {
                char c = (char) b;
                if ((c == delimiter[0]) && (detectDelimiter(in, delimiter))) {

                    out.openTextFragment(delimiter);
                    out.write(delimiter);
                    fillMinifyCodeBlockContent(out, content.toString());
                    out.write(delimiter);
                    out.closeTextFragment(delimiter);

                    return;
                } else {
                    fill(content, in);
                }
            }
            else {
                fill(content, in);
            }
        }
    }

    private void fillMinifyCodeBlockContent(TemplateWriter out, String content) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        {
            JsMin minifier = JsMin.builder()
                    .inputStream(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)))
                    .outputStream(new StripOutputStream(buffer))
                    .build();

            minifier.minify();
            buffer.flush();
        }

        String result = buffer.toString(String.valueOf(StandardCharsets.UTF_8)).trim();
        out.write(result);
    }

    private boolean detectDelimiter(StringReader in, char[] delimiter) throws IOException {
        in.reset();
        for (int i = 0; i < delimiter.length; i++) {
            int c = in.read();
            if (c != (int)delimiter[i]) {
                return false;
            }
        }
        return true;
    }


    static class StripOutputStream extends OutputStream {
        private final OutputStream buffer;
        private int writtenCount = 0;

        public StripOutputStream(OutputStream buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) throws IOException {
            if (writtenCount == 0) {
                if (isJunkCharacter(b)) {
                    return;
                }
            }
            this.buffer.write(b);
            this.writtenCount++;
        }

        private boolean isJunkCharacter(int c) {
            return (c == '\n');
        }
    }


    static class TemplateWriter {
        private static final int LITERAL_FRAGEMENT = 1;
        private static final int TEXT_FRAGEMENT = 2;
        private static final int PLACEHOLDER_FRAGEMENT = 3;

        private final List<IDslFragment> fragments;
        private final StringBuffer buffer;

        private int currentFragementType = LITERAL_FRAGEMENT;
        private int currentFragementOffset;
        private String currentFragementOpenDelimiter;

        public TemplateWriter(DslTemplate template) {
            this.fragments = template.fragments;
            this.buffer = template.getBuffer();
        }

        void openLiteralFragment() {
            if (this.currentFragementType != LITERAL_FRAGEMENT)
                throw new RuntimeException("invalid openLiteralFragment() operation, due to precede fragement don't close yet");

            int offset = this.buffer.length();
            if (offset > this.currentFragementOffset) {
                closeLiteralFragment();
            }

            this.currentFragementOffset = offset;
            this.currentFragementType = LITERAL_FRAGEMENT;
            this.currentFragementOpenDelimiter = null;
        }

        void openTextFragment(char openDelimiter) {
            openTextFragment(new String(new char[]{openDelimiter}));
        }

        void openTextFragment(char[] openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            openTextFragment(new String(openDelimiter));
        }

        void openTextFragment(String openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            if (this.currentFragementType != LITERAL_FRAGEMENT)
                throw new RuntimeException("invalid opentTextFragment() operation, due to precede fragement don't close yet");

            int offset = this.buffer.length();
            if (offset > this.currentFragementOffset) {
                closeLiteralFragment();
            }

            this.currentFragementOffset = offset;
            this.currentFragementType = TEXT_FRAGEMENT;
            this.currentFragementOpenDelimiter = openDelimiter;
        }

        void openPlaceholderFragment(char openDelimiter) {
            openPlaceholderFragment(new String(new char[]{openDelimiter}));
        }

        void openPlaceholderFragment(char[] openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            openPlaceholderFragment(new String(openDelimiter));
        }

        void openPlaceholderFragment(String openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            if (this.currentFragementType != LITERAL_FRAGEMENT)
                throw new RuntimeException("invalid openPlaceholderFragment() operation, due to precede fragement don't close yet");

            int offset = this.buffer.length();
            if (offset > this.currentFragementOffset) {
                closeLiteralFragment();
            }

            this.currentFragementOffset = offset;
            this.currentFragementType = PLACEHOLDER_FRAGEMENT;
            this.currentFragementOpenDelimiter = openDelimiter;
        }

        void closeTextFragment(char openDelimiter) {
            closeTextFragment(new String(new char[]{openDelimiter}));
        }

        void closeTextFragment(char[] openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            closeTextFragment(new String(openDelimiter));
        }

        void closeTextFragment(String closeDelimiter) {
            if (closeDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "closeDelimiter"));

            if (this.currentFragementType != TEXT_FRAGEMENT)
                throw new RuntimeException("invalid call closeTextFragment() without call openTextFragment() first.");

            int length = this.buffer.length() - this.currentFragementOffset;
            if (length > 0) {
                DslLiteralFragment literal = new DslLiteralFragment(this.buffer, this.currentFragementOffset, length);
                DslTextFragment f = new DslTextFragment(literal, this.currentFragementOpenDelimiter, closeDelimiter);
                fragments.add(f);

                this.currentFragementOffset = this.buffer.length();
            }

            this.currentFragementType = LITERAL_FRAGEMENT;
            this.currentFragementOpenDelimiter = null;
        }

        void closePlaceholderFragment(char openDelimiter) {
            closePlaceholderFragment(new String(new char[]{openDelimiter}));
        }

        void closePlaceholderFragment(char[] openDelimiter) {
            if (openDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "openDelimiter"));

            closePlaceholderFragment(new String(openDelimiter));
        }

        void closePlaceholderFragment(String closeDelimiter) {
            if (closeDelimiter == null)
                throw new IllegalArgumentException(String.format("specified argument '%s' cannot be null", "closeDelimiter"));

            if (this.currentFragementType != PLACEHOLDER_FRAGEMENT)
                throw new RuntimeException("invalid call openPlaceholderFragment() without call closePlaceholderFragment() first.");

            int length = this.buffer.length() - this.currentFragementOffset;
            if (length > 0) {
                DslLiteralFragment literal = new DslLiteralFragment(this.buffer, this.currentFragementOffset, length);
                DslPlaceholderFragement f = new DslPlaceholderFragement(literal, this.currentFragementOpenDelimiter, closeDelimiter);
                fragments.add(f);

                this.currentFragementOffset = this.buffer.length();
            }

            this.currentFragementType = LITERAL_FRAGEMENT;
            this.currentFragementOpenDelimiter = null;
        }

        void closeLiteralFragment() {
            if (this.currentFragementType != LITERAL_FRAGEMENT)
                throw new RuntimeException("invalid call closeLiteralFragment() operation, due to precede fragement don't close yet");

            int length = this.buffer.length() - this.currentFragementOffset;
            if (length > 0) {
                DslLiteralFragment f = new DslLiteralFragment(this.buffer, this.currentFragementOffset, length);
                fragments.add(f);
            }

            this.currentFragementType = LITERAL_FRAGEMENT;
            this.currentFragementOpenDelimiter = null;
        }

        void write(int b) {
            this.buffer.append((char)b);
        }

        void write(char c) {
            this.buffer.append(c);
        }

        void write(String str) {
            this.buffer.append(str);
        }

        void write(char[] str) {
            this.buffer.append(str);
        }
    }
}
