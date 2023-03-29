package com.bofry.databroker.core.component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JRubyUtils {

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");

    private static final JRubyUtils instance = new JRubyUtils();

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    // TODO 可以log出執行結果或錯誤，方便Debug。
    // FIXME 如果 indexScript = null or 空, 會噴 NullPointerException。
    public static String evalScriptAsString(String script, Object o) throws Exception {
        engine.put("ctx", o);
        Reader inputString = new StringReader(String.format("\"%s\"", script));
        BufferedReader reader = new BufferedReader(inputString);
        Object result = engine.eval(reader);
        return result.toString();
    }

    // TODO 可以log出執行結果或錯誤，方便Debug。
    // FIXME 如果 indexScript = null or 空, 會噴 NullPointerException。
    public static boolean evalScriptAsBool(String script, Object o) throws Exception {
        engine.put("ctx", o);
        Reader inputString = new StringReader(String.format("%s", script));
        BufferedReader reader = new BufferedReader(inputString);
        Object result = engine.eval(reader);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return false;
    }

    public static void lock() {
        instance.reentrantReadWriteLock.writeLock().lock();
    }

    public static void unlock() {
        instance.reentrantReadWriteLock.writeLock().unlock();
    }

    public static JRubyUtils getInstance() {
        return instance;
    }

}
