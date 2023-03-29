# 01_KafkaToStdout.yaml
kafka key/message:
```
jj@test:{"provider":"PP","player":"jj","bet":888.88}
```

# 02_KafkaToElasticsearch.yaml
kafka key/message:
```
jj@test:{"ticketId":"2112040000000001","provider":5432,"player":"JJ","startAt":1632710379,"round":2112040000000009}
```

# 03_KafkaToKafka.yaml
kafka key/message:
```
jj@test:{"provider":"54321","player":"JJ","bet":99.888}
```

# 04_KafkaToSQLite.yaml
kafka key/message:
```
jj@test:{"player":"JJ","bet":777.55,"timestamp":1628880300000}
```

# 05_KafkaToMysql.yaml
kafka key/message:
```
jj@test:{"player":"JJ","bet":555.444,"timestamp":1628880300000}
```
table scheam:
```
CREATE DATABASE `testdb`;
CREATE TABLE `testdb`.`bet` (`id` serial,`player` text,`bet` decimal(10,2),`create_date` timestamp, PRIMARY KEY (id));
```

# 06_KafkaToPostgresql.yaml
kafka key/message:
```
jj@test:{"player":"JJ","bet":9999.88,"timestamp":1628880200000,"symbol":["a","b","c"]}
```
table scheam:
```
CREATE DATABASE "testdb";
CREATE TABLE "public"."bet" ("player" varchar,"bet" numeric,"create_date" timestamp,"symbol" _varchar);
```

# 07_KafkaToCassandra.yaml
kafka key/message:
```
jj@test:{"id":2021120400000001,"memo":{"test":"test"},"timestamp":1628880300}
```
table scheam:
```
CREATE KEYSPACE "test" WITH REPLICATION = {'class': 'SimpleStrategy','replication_factor': 1};
USE "test";
CREATE TABLE "test"."test_table" ("id" bigint,"memo" map<ascii,ascii>,"create_date" TIMESTAMP, PRIMARY KEY (id));
```

# 90_MappingTestType.yaml
kafka key/message:
```
jj@test:{"b":false,"s":"Hello World","i":2147483647,"l":9223372036854775807,"dm":123456789.987654321,"da":1639464144934,"ti":1639464144934,"tst":1639464144}
```

# 91_MappingTestDefault.yaml
kafka key/message:
```
jj@test:{}
```