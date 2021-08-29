package com.qiunan;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * Unit test for simple App.
 */
public class AppTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

        StreamTableEnvironment tenv = StreamTableEnvironment.create(env, settings);

//        clickhouse 创建本地表
//        CREATE TABLE test(
//          id Int32,
//          name String,
//          age Int64,
//          rate Float32
//        ) ENGINE = MergeTree
//        ORDER BY (id);

        // create source table
        tenv.executeSql("CREATE TEMPORARY TABLE clickhouse_source (\n" +
                "  id INT,\n" +
                "  name VARCHAR,\n" +
                "  age BIGINT,\n" +
                "  rate FLOAT\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'rows-per-second' = '50'\n" +
                ")");

        // create sink table
        tenv.executeSql("CREATE TEMPORARY TABLE clickhouse_output (\n" +
                "  id INT,\n" +
                "  name VARCHAR,\n" +
                "  age BIGINT,\n" +
                "  rate FLOAT\n" +
                ") WITH (\n" +
                "  'connector' = 'clickhouse',\n" +
                "  'url' = 'clickhouse://localhost:8123',\n" +
                "  'username' = '',\n" +
                "  'password' = '',\n" +
                "  'table-name' = 'test'\n" +
                ")");

        tenv.executeSql("INSERT INTO clickhouse_output\n" +
                "SELECT \n" +
                "  id,\n" +
                "  name,\n" +
                "  age,\n" +
                "  rate\n" +
                "FROM clickhouse_source");
    }
}

