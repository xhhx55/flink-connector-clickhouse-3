# ClickHuouse结果表

## 什么是ClickHouse

ClickHouse是一个用于联机分析（OLAP）的列式数据库管理系统，详情请参见[什么是ClickHouse](https://clickhouse.tech/docs/zh/?spm=a2c4g.11186623.0.0.45a7bcc24qHFW1)。

## 前提条件

已创建ClickHouse表，详情请参见[创建表](https://clickhouse.tech/docs/zh/getting-started/tutorial/?spm=a2c4g.11186623.0.0.45a7bcc24qHFW1#create-tables)

## 使用限制

仅Flink计算引擎VVR 3.0.2及以上版本支持ClickHouse Connector。

## DDL定义

```sql
CREATE TABLE clickhouse_sink (
  id INT,
  name VARCHAR,
  age BIGINT,
  rate FLOAT
) WITH (
  'connector' = 'clickhouse',
  'url' = '<yourUrl>',
  'username' = '<yourUsername>',
  'password' = '<yourPassword>',
  'table-name' = '<yourTablename>',
  'sink.batch-size' = '8000',
  'sink.flush-interval' = '1000',
  'sink.max-retries' = '3',
)
```

## WITH参数

| **Option**                  | **Required** | **Default** | **Type** | **Description**                                              |
| :-------------------------- | :----------- | :---------- | :------- | ------------------------------------------------------------ |
| **connector**               | required     | (none)      | String   | Specify what connector to use, for ClickHouseuse `'clickhouse'`. |
| **url**                     | required     | (none)      | String   | the ClickHouse url in format `clickhouse://<host>:<port>`.   |
| **username**                | optional     | (none)      | String   | the ClickHouse username.                                     |
| **password**                | optional     | (none)      | String   | the ClickHouse password.                                     |
| **database-name**           | optional     | 'default'   | String   | the ClickHouse database name. Default to `default`.          |
| **table-name**              | required     | (none)      | String   | the ClickHouse table name.                                   |
| **sink.batch-size**         | optional     | 1000        | Integer  | the flush max size, over this number of records, will flush data. The default value is 1000. |
| **sink.flush-interval**     | optional     | 1s          | Duration | the flush interval mills, over this time, asynchronous threads will flush data. The default value is 1s. |
| **sink.max-retries**        | optional     | 3           | Integer  | he max retry times if writing records to database failed.    |
| **sink.write-local**        | optional     | false       | Boolean  | directly write to local tables in case of Distributed table. |
| **sink.partition-strategy** | optional     | 'balanced'  | String   | partition strategy. available: balanced, hash, shuffle.      |
| **sink.partition-key**      | optional     | (none)      | String   | partition key used for hash strategy.                        |
| **sink.ignore-delete**      | optional     | true        | Boolean  | whether to treat update statements as insert statements and ignore deletes. defaults to true. |

## 类型映射

| ClickHouse字段类型 | Flink字段类型 |
| :----------------- | :------------ |
| UInt8              | BOOLEN        |
| Int8               | TINYINT       |
| Int16              | SMALLINT      |
| Int32              | INTEGER       |
| Int64              | BIGINT        |
| Float32            | FLOAT         |
| Float64            | DOUBLE        |
| FixedString        | CHAR          |
| String             | VARCHA        |
| FixedString        | BINARY        |
| String             | VARBINARY     |
| Date               | DATE          |
| DateTime           | TIMESTAMP(0)  |
| Datetime64(x)      | TIMESTAMP(x)  |
| Decimal            | DECIMAL       |
| Array              | ARRAY         |
| 不支持             | TIME          |
| 不支持             | MAP           |
| 不支持             | MULTISET      |
| 不支持             | ROW           |

## 代码示例

```sql
CREATE TEMPORARY TABLE clickhouse_source (
  id INT,
  name VARCHAR,
  age BIGINT,
  rate FLOAT
) WITH (
  'connector' = 'datagen',
  'fields.id.min' = 1,
  'fields.id.min' = 50,
  'rows-per-second' = '50'
);

CREATE TEMPORARY TABLE clickhouse_output (
  id INT,
  name VARCHAR,
  age BIGINT,
  rate FLOAT
) WITH (
  'connector' = 'clickhouse',
  'url' = '<yourUrl>',
  'username' = '<yourUsername>',
  'password' = '<yourPassword>',
  'table-name' = '<yourTablename>'
);

INSERT INTO clickhouse_output
SELECT 
  id,
  name,
  age,
  rate
FROM clickhouse_source;
```

