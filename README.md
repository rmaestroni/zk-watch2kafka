This is a work in progress

## Configuration

Sample configuration

```json
{
  "watches": [
    {
      "zookeeper": "localhost:2181",
      "znode": "foo-bar",
      "operation": "GET_DATA",

      "kafka": "localhost:9092",
      "target_topic": "zookeeper-watch-events",

      "transactional_id": "some-transactional-id",
      "max_transaction_retries": 10,

      "enable_idempotence": true,
      "acks": "",
      "retries": -1,

      "serializer": "cloud.thh.zk_watch2kafka.kafka.serializers.AvroSerializer"
    },
    {
      "zookeeper": "localhost:2181",
      "znode": "bar-baz",
      "operation": "GET_CHILDREN",

      "kafka": "localhost:9092",
      "target_topic": "zookeeper-watch-events",

      "transactional_id": null,
      "max_transaction_retries": -1,

      "enable_idempotence": true,
      "acks": "all",
      "retries": 10,

      "serializer": "cloud.thh.zk_watch2kafka.kafka.serializers.BsonSerializer"
    }
  ]
}
```

Every item in `watches` is an object consisting of

  * `zookeeper` - Zookeeper connection string;
  * `znode` - Znode to watch;
  * `operation` - Either `GET_DATA` or `GET_CHILDREN`;
  * `kafka` - Kafka brokers list, comma separated;
  * `target_topic` - The topic name to write to;
  * `transactional_id` - When present it uses a transactional Kafka producer,
     in this case `enable_idempotence`, `acks`, `retries` are ignored and they
     default to `true`, `"all"`, `Integer.MAX_VALUE` as described in
     [KafkaProducer javadoc](http://kafka.apache.org/22/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html). Set it to `null` to use the traditional, non-transactional
     producer;
  * `max_transaction_retries` - How many times a transaction should retry before
     failing. It's used only when `transactional_id` is present and ignored
     otherwise;
  * `enable_idempotence` - Whether to use an idempotent producer or not
    (non-transactional only), when `true` the other options `acks` and `retries`
    are ignored;
  * `acks` - Producer required acks (non-transactional only with `enable_idempotence=false`);
  * `retries` - Producer retries (non-transactional only with `enable_idempotence=false`);
  * `serializer` - The serializer qualified class name, defining how to serialize
    the value to Kafka. The class is expected to extend
    `cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer`.
