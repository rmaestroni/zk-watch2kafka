package cloud.thh.zk_watch2kafka.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public abstract class Producer {
  private WatchConfig config;

  public static Producer buildProducer(WatchConfig config) {
    if (null != config.transactionalId) {
      return new TransactionalProducer(config);
    } else {
      return new NonTransactionalProducer(config);
    }
  }

  protected Producer(WatchConfig config) {
    this.config = config;
  }

  public abstract void produce(String key, byte[] value);

  ProducerRecord<String, byte[]> buildRecord(String key, byte[] value) {
    return new ProducerRecord<>(config.targetTopic, key, value);
  }

  protected WatchConfig getConfig() {
    return config;
  }
}
