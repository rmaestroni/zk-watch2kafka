package cloud.thh.zk_watch2kafka.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class TransactionalProducer extends Producer {
  private KafkaProducer<String, byte[]> kafka;

  TransactionalProducer(WatchConfig config) {
    super(config);
    this.kafka = buildKafkaProducer();
  }

  @Override
  public void produce(String key, byte[] value) {
    // TODO Auto-generated method stub
  }

  private KafkaProducer<String, byte[]> buildKafkaProducer() {
    Properties props = new Properties();
    props.put("bootstrap.servers", getConfig().kafka);
    props.put("transactional.id", getConfig().transactionalId);

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
