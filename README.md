# zk-watch2kafka

[![Maintainability](https://api.codeclimate.com/v1/badges/a4dbb590796b1cec7f93/maintainability)](https://codeclimate.com/github/rmaestroni/zk-watch2kafka/maintainability)
![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/rmaestroni/zk-watch2kafka.svg)
![Docker Pulls](https://img.shields.io/docker/pulls/rmaestroni/zk-watch2kafka.svg)

Set watches on ZooKeeper znodes and produce them as events to Kafka.

There are two operations you can associate with a watcher

  * `GET_DATA`: the watcher is triggered when the znode data is changed or the
    znode is deleted;
  * `GET_CHILDREN`: the watcher is triggered when a new node is added as a children
    of the watched node, or an existing children is deleted or renamed.

Zk-watch2kafka can operate on multiple ZooKeeper ensemble and produce as well
to many Kafka clusters.

The operations, znodes, source and destination clusters are defined through a JSON
configuration file. See the section below for an example and a detailed description
of the options.

When the program starts it produces one event for each watch set. Note that for a
`GET_DATA` operation, if the target znode is deleted you won't see any event in
case a node with the same name will be re-created afterwards.

From an high-level perspective the program implements this loop:

  1. Given a znode, apply the operation and set a watcher;
  2. Produce an event with the result of the operation;
  3. When a watcher triggers, go back to (1).

Hence **there's no guarantee the program will trigger an event for every change to a
znode.** This is because between 3 and 1 (i.e. after the watcher is triggered
and before a new watcher is set), an arbitrary amount of changes could be applied
to the znode. However it is guaranteed that at least one event representing the
"last state" of the znode will be produced.

See the [ZooKeeper documentation](http://zookeeper.apache.org/doc/r3.4.14/zookeeperProgrammers.html#sc_zkDataMode_watches)
for more informations.

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

  * `zookeeper` - ZooKeeper connection string;
  * `znode` - Znode to watch;
  * `operation` - Either `GET_DATA` or `GET_CHILDREN`;
  * `kafka` - Kafka brokers list, comma separated;
  * `target_topic` - The topic name to write to;
  * `transactional_id` - When present it uses a transactional Kafka producer,
     in this case `enable_idempotence`, `acks`, `retries` are ignored and they
     default to `true`, `"all"`, `Integer.MAX_VALUE` as described in
     [KafkaProducer javadoc](http://kafka.apache.org/22/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html).
     Set it to `null` to use the traditional, non-transactional producer;
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

## Usage

The program expects the path to the configuration file as the only command line
parameter. With Docker for instance you can mount the configuration and run with

```bash
docker run --rm \
  -v "_full_path_to_config_:/app/config.json" \
  -e LOG_LEVEL="WARN" \
  -e JAVA_OPTIONS="-Xmx128m" \
  rmaestroni/zk-watch2kafka \
  config.json
```

Take a look at `entrypoint.sh` to run zk-watch2kafka natively. Essentially
you need to set up accordingly your classpath with `-cp` and call the main class
`cloud.thh.zk_watch2kafka.App` passing the JSON config file as parameter.

## Build

### Build with Docker

```bash
docker build -t zk-watch2kafka .
```

(or use any other tag as your discretion).

### Build without Docker

This project was tested with Java 11 and Maven 3.6, although it may work with
other versions.

```bash
mvn package && mvn dependency:copy-dependencies
```

### Development setup

```bash
docker-compose up -d
```

Will run zk-watch2kafka along with a ZooKeeper and a Kafka container.

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
