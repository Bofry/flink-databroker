package com.bofry.databroker.core.misc;

import com.bofry.databroker.core.component.IValueAssigner;
import com.bofry.databroker.core.component.JacksonUtils;
import com.bofry.databroker.core.component.RawContent;
import com.bofry.databroker.core.redis.stream.*;
import com.bofry.databroker.core.script.type.Timestamp;
import com.bofry.databroker.core.serdes.DefaultAssignerDeserializer;
import com.bofry.databroker.core.redis.stream.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscTest {

    public static void main(String[] args) {
        testRubyScriptBoolean();
    }

    public static void demoRedisStreamConsumer_std() {
        RedisURI redisUri = RedisURI.Builder.redis("localhost").withPort(6379).withDatabase(1).build();

        RedisClient client = RedisClient.create(redisUri);
        LettuceStreamConnection connection = LettuceStreamConnectionFactory.create(client);
        connection.connect();

        LettuceStreamAdapter adapter = new LettuceStreamAdapter(connection);
        final RedisStreamConsumer consumer = new RedisStreamConsumer(adapter,
                RedisStreamConsumerProperties.create()
                        .setGroup("javagroup")
                        .setName("javademo")
                        .setMaxInFlight(1)
                        .setMaxPollingTimeout(Duration.ofMillis(100))
                        .setClaimMinIdleTime(Duration.ofSeconds(3))
                        .setIdlingTimeout(Duration.ofMillis(300))
                        .setClaimSensitivity(1)
                        .setClaimOccurrenceRate(5));

        try {
            consumer.subscribe(new RedisStreamOffset[]{
                    RedisStreamOffset.fromNeverDelivered("foo-stream")
            }, new IRedisStreamMessageHandler() {
                @Override
                public void process(RedisStreamMessage message) {
                    System.out.printf("stream: %s\n", message.getStream());
                    System.out.printf("id    : %s\n", message.getId());
                    System.out.printf("body  :\n");

                    for (Map.Entry<String, String> item : message.getBody().entrySet()) {
                        System.out.printf("  %s = %s\n", item.getKey(), item.getValue());
                    }
                    message.commitAndExpel();
                }
            });

        } catch (Exception ex) {
            System.out.println(">>> Exception");
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    public static void testRubyScriptBoolean() {
        Object o = TestModel.builder()
                .status("SETTLED")
                .symbol(RawContent.builder()
                        .name("symbol")
                        .data("[30001]")
                        .build())
                .build();

        // String script = "defined?(ctx.round) != nil && ctx.round.to_s.length > 0";
        // String script = "ctx.status == 'SETTLED' && (ctx.round == nil || ctx.round.length == 0)";
        String script = "ctx.status != 'CANCELLED' && (ctx.status == 'RUNNING' || ctx.status == 'SETTLED')";

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        engine.put("ctx", o);
        Reader inputString = new StringReader(String.format("%s", script));
        BufferedReader reader = new BufferedReader(inputString);
        Object result = engine.eval(reader);
        System.out.println(((Boolean) result).toString());
    }

    public static void semaphoreExample() {
        Semaphore semaphore = new Semaphore(2);
        System.out.println(semaphore.availablePermits());
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String tname = Thread.currentThread().getName();
                System.out.println(String.format("老司機：%s，停車場外排隊，時間：%s", tname, new Date()));
                try {
                    // 執行此行，讓所有線程先排隊等待進入停車場
                    Thread.sleep(100);
                    // 執行阻塞
                    semaphore.acquire();
                    System.out.println(semaphore.availablePermits());
                    System.out.println(String.format("老司機：%s，已進入停車場，時間：%s", tname, new Date()));
                    Thread.sleep(1000);
                    System.out.println(String.format("老司機：%s，離開停車場，時間：%s", tname, new Date()));
                    // 釋放鎖
                    semaphore.release();
                    System.out.println(semaphore.availablePermits());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        threadPool.submit(runnable);
        threadPool.submit(runnable);
        threadPool.submit(runnable);
        threadPool.submit(runnable);
        threadPool.submit(runnable);
        threadPool.shutdown();
    }

    @SneakyThrows
    public static void URLClassLoader() {

        URL[] urls = new URL[]{new File("/Users/morose/Documents/IntelliJ-workspace/GAAS/HelloWorld/src/hello.jar").toURI().toURL()};
        URLClassLoader cl = new URLClassLoader(urls);
        System.out.println(cl);
    }

    public static void teatArrayBinarySearchAdd() {
        List<Integer> al = new ArrayList<Integer>();
        al.add(0);
        al.add(100);

        Integer i = 50;

        int index = Collections.binarySearch(al, i);
        System.out.println(index);

        if (index < 0) {
            al.add(~index, i);
        } else {
            throw new IllegalArgumentException();
        }

        System.out.println(al);
    }

    public static void testRouteRegex() {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._.-]+");
        System.out.println(pattern.matcher("round-abc_123").matches());
    }

    public static void multipleThreadJRubyTest() {
        Runnable task1 = MiscTest::testRubyScriptEngineOne;
        Runnable task2 = MiscTest::testRubyScriptEngineOne;
        Runnable task3 = MiscTest::testRubyScriptEngineOne;
        Runnable task4 = MiscTest::testRubyScriptEngineOne;
        Runnable task5 = MiscTest::testRubyScriptEngineOne;
        Runnable task6 = MiscTest::testRubyScriptEngineOne;
        Runnable task7 = MiscTest::testRubyScriptEngineOne;
        Runnable task8 = MiscTest::testRubyScriptEngineOne;
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);
        Thread t3 = new Thread(task3);
        Thread t4 = new Thread(task4);
        Thread t5 = new Thread(task5);
        Thread t6 = new Thread(task6);
        Thread t7 = new Thread(task7);
        Thread t8 = new Thread(task8);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
    }

    public static void ScheduledExecutorServiceTest() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        Runnable task = () -> System.out.println("Hello");
        scheduledThreadPool.scheduleAtFixedRate(task, 1, 5, TimeUnit.SECONDS);
    }

    public static void StringBuilderTest() {
        final StringBuilder sb = new StringBuilder();
        sb.append("abcd1234");
        System.out.println(sb.toString());
        sb.delete(1, 4);
        System.out.println(sb.toString());
        sb.insert(1, "bcd");
        System.out.println(sb.toString());
        System.out.println(sb.substring(1, 4));
        sb.setLength(0);
        System.out.println("'" + sb.toString() + "'");
        sb.append("abcd1234");
        System.out.println(sb.toString());
    }

    @SneakyThrows
    public static void testYamlForceMyClass() {
        String yaml = "myObject:  !ref { field: round}";
//        String yaml = "myObject: 1234";
        YamlModel o = JacksonUtils.YAML_OBJECT_MAPPER.readValue(yaml, YamlModel.class);
        System.out.println(o);
    }

    @SneakyThrows
    public static void testYamlForceType() {
        String yaml = "" +
                "a: 123                     # 整數\n" +
                "b: \"123\"                   # 字串（使用雙引號）\n" +
                "c: 123.0                   # 浮點數\n" +
                "d: !!float 123             # 浮點數，使用!!表達的嚴格型態\n" +
                "e: !!str 123               # 字串，使用嚴格型態\n" +
                "f: !!str Yes               # 字串，使用嚴格型態\n" +
                "g: Yes                     # 布林值\"真\"\n" +
                "h: Yes we have No bananas  # 字串（包含\"Yes\"和\"No\"）";
        Object o = JacksonUtils.YAML_OBJECT_MAPPER.readValue(yaml, YamlModel.class);
        System.out.println(o);
    }

    @SneakyThrows
    public static void testRubyScriptEngineOne() {
        Object o = TestModel.builder()
                .round("2021093000011234L")
                .build();

        String script = "\"gaas-round-dev-#{ctx.round.to_s[0..5]}\"";

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        engine.put("ctx", o);
        Reader inputString = new StringReader(script);
        BufferedReader reader = new BufferedReader(inputString);
        Object result = engine.eval(reader);
        System.out.println(result.toString());
    }

    @SneakyThrows
    public static void testRubyScriptEngineTwo() {
        ScriptEngine jruby = new ScriptEngineManager().getEngineByName("jruby");
        String script = "ctx = {\n" +
                "  'id'         => 2109281632834957495,\n" +
                "  'round'      => 2005260000000000001,\n" +
                "  'player'     => \"luffy\",\n" +
                "  'provider'   => \"onepiece\",\n" +
                "  'session'    => \"session-12345\",\n" +
                "  'game'       => \"thelostworld\",\n" +
                "  'room'       => \"01\",\n" +
                "  'round_type' => \"FREE_BONUS\",\n" +
                "}\n" +
                "\n" +
                "\"id:#{ctx['id']}:round_type:#{ctx['round_type']}\"";
        Reader inputString = new StringReader(script);
        BufferedReader reader = new BufferedReader(inputString);
        String t = (String) jruby.eval(reader);
        System.out.println(t);
    }

    @SneakyThrows
    public static void testRubyScriptIsNull() {
        Object o = TestModel.builder()
                .round("2021093000011234L")
                .build();

        String script = "";

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby");
        engine.put("ctx", o);
        Reader inputString = new StringReader(script);
        BufferedReader reader = new BufferedReader(inputString);
        Object result = engine.eval(reader);
        System.out.println(result.toString());
    }

    @SneakyThrows
    public static void testJavaScriptEngine() {
        // eval script
        Object o;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        String docIdScript = "var a = ctx.vendor + 1;" +
                "return a";
        String script = String.format("var res = (function(ctx){%s})(%s); res;", docIdScript, "{\"player\":\"JJ\",\"vendor\":10547}");
        o = engine.eval(script);
        engine.eval("if(typeof(res)!='string') throw 'Parameter is not a string!';");
        System.out.println(o);
    }

    public static void testRegex() {
        List<String> names = new ArrayList<>();

        String SQL_INSERT = "INSERT INTO `testdb`.`EMPLOYEE` (`NAME`, `SALARY`, `CREATED_DATE`) " +
                "VALUES (%{name}, %{salary}, %{create_date});";

        String pattern = "%\\{[\\w]+}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(SQL_INSERT);
        while (matcher.find()) {
            String name = matcher.group(0);
            names.add(name.substring(2, name.length() - 1));
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            SQL_INSERT = subexpr.matcher(SQL_INSERT).replaceAll("?");
        }
        System.out.println(names);
        System.out.println(SQL_INSERT);
    }

    public static void testPing() {
        try {
            InetAddress address = InetAddress.getByName("127.0.0.2");
            boolean reachable = address.isReachable(5000);
            System.out.println("Is host reachable? " + reachable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void testHttpClient() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://raw.githubusercontent.com/MoroseDog/test/main/kafkaToMysql.yaml");
        // Create a custom response handler
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        String responseBody = httpclient.execute(httpget, responseHandler);
    }

    public static void configReplaceEnvironmentTest() {
        String conf = "source:\n" +
                "  - type: kafka\n" +
                "    config:\n" +
                "      topic: 'source-topic-${Elasticsearch_Business_Address}'\n" +
                "      properties:\n" +
                "        bootstrap.servers: '${Elasticsearch_Business_Address}'\n" +
                "        group.id: flink-test-group\n" +
                "sink:\n" +
                "  - type: elasticsearch\n" +
                "    config:\n" +
                "      class_name: com.bofry.stdout.Template\n" +
                "      properties:\n" +
                "        host: '${Elasticsearch_Business_Address}'\n" +
                "        user: '${Elasticsearch_Business_Username}'\n" +
                "        password: '${Elasticsearch_Business_Password}'\n" +
                "        method: 'GET'\n" +
                "        index: 'gaas-bet-${Environment}-'\n" +
                "        partition: '210816' # TODO script\n" +
                "        action: '/_update'\n" +
                "        document_id: 1 #TODO script\n" +
                "        dsl: |\n" +
                "          '{\n" +
                "             \"script\": {\n" +
                "               \"source\": \"ctx._source.vendor = params.vendor;ctx._source.player = params.player;\",\n" +
                "               \"lang\": \"painless\",\n" +
                "               \"params\": %s\n" +
                "             },\n" +
                "             \"upsert\": %s\n" +
                "           }'\n" +
                "    mapping:\n" +
                "      - name: vendor\n" +
                "        type: string\n" +
                "        tag: '{\"required\",\"notEmpty\",\"notNull\",\"notZero\"}'\n" +
                "        source: provide\n" +
                "        _default: pp\n" +
                "      - name: player\n" +
                "        type: string\n" +
                "        tag: '{\"required\",\"notEmpty\",\"notNull\",\"notZero\"}'\n" +
                "        source: player\n" +
                "        _default: jj\n" +
                "\n";

        Map<String, String> envMap = System.getenv();
        String pattern = "\\$\\{([A-Za-z0-9_]+)}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(conf);
        while (matcher.find()) {
            String envValue = envMap.get(matcher.group(1));
            if (envValue == null) {
                envValue = "";
            } else {
                envValue = envValue.replace("\\", "\\\\");
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            conf = subexpr.matcher(conf).replaceAll(envValue);
        }

        System.out.println(conf);
    }

    @SneakyThrows
    public static void simpleElasticsearchTest() {
        // URI requestURI = URI.create("https://search-gaas-uat-test-dev-ny6abctny4ic7oib4yvq3nccui.ap-northeast-1.es.amazonaws.com");
        URI requestURI = URI.create("http://localhost:9200");

        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("gaas-user", "Tech168!^*"));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(requestURI.getHost(), requestURI.getPort(), requestURI.getScheme()))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.disableAuthCaching();
                    return httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider);
                });

        RestClient restClient = builder.build();

        Request request = new Request(
                "POST",
                "/test/_update/1");

        String dsl = "{\n" +
                "  \"script\": {\n" +
                "    \"source\": \"ctx._source.name = params.name\",\n" +
                "    \"lang\": \"painless\",\n" +
                "    \"params\": %s" +
                "  },\n" +
                "  \"upsert\": %s" +
                "}";

        dsl = String.format(dsl, "{\n" +
                "      \"name\": \"JJ\"\n" +
                "    }", "{\n" +
                "      \"name\": \"\"\n" +
                "    }");

        request.setJsonEntity(dsl);

        Response response = null;
        try {
            response = restClient.performRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(response);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);

        restClient.close();
        System.out.println("Finish");
    }

    @SneakyThrows
    public static void simpleJDBCTest() {
        String DB_URL = "jdbc:mysql://127.0.0.1:3306/testdb?allowMultiQueries=true";
        String USER = "root";
        String PASS = "root";

        List<String> names = new ArrayList<>();

        String jsonStr = "{\n" +
                "   \"name\":\"JJ\",\n" +
                "   \"salary\":\"99999.99\",\n" +
                "   \"create_date\":1628880299123\n" +
                "}";

        Object o = JacksonUtils.toObject(jsonStr, MysqlTestModel.class);

        // SQL 語法
        String SQL_INSERT = "INSERT INTO `testdb`.`bet` (`player`, `bet`, `create_date`) VALUES (%{name}, %{salary}, %{create_date});" +
                "INSERT INTO `testdb`.`bet` (`player`, `bet`, `create_date`) VALUES ('KK', %{salary}, %{create_date});";
//        String SQL_INSERT = "INSERT INTO `testdb`.`EMPLOYEE` (`NAME`, `SALARY`) VALUES (%{name}, %{salary});";

        // 取出要汰換的 value 名稱，並將其 SQL 語法換成 ? 符號
        String pattern = "%\\{[\\w]+}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(SQL_INSERT);
        while (matcher.find()) {
            String name = matcher.group(0);
            names.add(name.substring(2, name.length() - 1));
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            SQL_INSERT = subexpr.matcher(SQL_INSERT).replaceAll("?");
        }

        // 資料庫連線
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

        // 準備stmt
        PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT);

        // 根據 names 去 source map 裡面去值並帶入
        // see also table 5.2 : https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-type-conversions.html
        Class c = MysqlTestModel.class;
        for (int i = 0; i < names.size(); i++) {
            Object v = c.getDeclaredField(names.get(i)).get(o);
            preparedStatement.setObject(i + 1, v);
        }
        preparedStatement.execute();
    }

}

@Data
@Builder
class TestModel implements Serializable {
    public String round;
    public String status;
    public RawContent symbol;
}

@Data
class YamlModel implements Serializable {
    public Integer a;
    public String b;
    public Float c;
    public Float d;
    public String e;
    public String f;
    public Boolean g;
    public String h;
    @JsonDeserialize(using = DefaultAssignerDeserializer.class)
    public IValueAssigner myObject;
}

@Data
class MysqlTestModel implements Serializable {
    @JsonProperty(required = true)
    public String name;
    public BigDecimal salary;
    public Timestamp create_date;
}