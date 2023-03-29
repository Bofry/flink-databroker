package com.bofry.databroker.core.component;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ElasticsearchBulkBufferTest {

    private static ElasticsearchBulkBuffer buffer;
    private static ElasticsearchMetadataItem capacityTestData;
    private static ElasticsearchMetadataItem maxSizeTestData;

    @BeforeClass
    public static void beforeClass() {
        ElasticsearchProperties properties = new ElasticsearchProperties();
        properties.setBulkFlushCapacity(10);
        properties.setBulkFlushMaxSize(4096);

        buffer = new ElasticsearchBulkBuffer(properties);

        capacityTestData = ElasticsearchMetadataItem.builder()
                .metadata("{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }")
                .build();

        maxSizeTestData = ElasticsearchMetadataItem.builder()
                .metadata("{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }")
                .body("{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }")
                .build();
    }

    @Test
    public void a_ShoutFlushTest() {
        Assert.assertFalse(buffer.shouldFlush());
    }

    @Test
    public void b_IsEmptyTest() {
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void c_WriteNullTest() {
        buffer.write(null);
    }

    @Test
    public void d_WriteTenTimesTest() {
        for (int i = 0; i < 10; i++) {
            buffer.write(capacityTestData);
        }
    }

    @Test
    public void e_ShoutFlushTest() {
        Assert.assertTrue(buffer.shouldFlush());
    }

    @Test
    public void f_testReadTest() {
        String outputBuffer = "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"player\":\"JJ\"} }\n";
        Assert.assertEquals(buffer.read(), outputBuffer);
    }

    @Test
    public void g_ShoutFlushTest() {
        Assert.assertFalse(buffer.shouldFlush());
    }

    @Test
    public void h_IsEmptyTest() {
        Assert.assertTrue(buffer.isEmpty());
    }

    @Test
    public void i_WriteSixTimesTest() {
        for (int i = 0; i < 6; i++) {
            buffer.write(maxSizeTestData);
        }
    }

    @Test
    public void j_ShoutFlushTest() {
        Assert.assertTrue(buffer.shouldFlush());
    }

    @Test
    public void k_testReadTest() {
        String outputBuffer = "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }\n" +
                "{ \"index\" : { \"_id\" : \"1\", \"_type\" : \"_doc\", \"_index\" : \"index\"} }\n" +
                "{ \"script\": {\"source\": \"ctx._source.parent_round = params.parent_round;\",\"lang\": \"painless\",\"params\": {\"player\":\"JJ\"}},\"upsert\": {\"round\":\"2110081633655082242\",\"provider\":\"10547\",\"session\":\"d3921407-b356-4b2e-9d46-67585c3b4428\",\"player\":\"JJ01\",\"totalBet\":1.00000,\"grossTotalBet\":1.00000,\"totalWin\":0.00000,\"totalRefund\":0,\"startAt\":1633655082,\"finishAt\":1633655082,\"game\":\"100101101\",\"account\":\"11324436\",\"accountType\":\"1\",\"dealer\":\"PP\",\"dealerPlayer\":\"12907219\",\"currency\":\"CNY\",\"wallet\":\"CNY\",\"betCount\":10,\"casino\":\"\",\"room\":\"1\",\"roundType\":\"NORMAL\",\"playerIp\":\"49.237.19.233\",\"playerOs\":\"Android\",\"playerDevice\":\"Mobile\",\"timezone\":\"UTC\"} }\n";
        Assert.assertEquals(buffer.read(), outputBuffer);
    }

    @Test
    public void l_IsEmptyTest() {
        Assert.assertFalse(buffer.isEmpty());
    }

}
