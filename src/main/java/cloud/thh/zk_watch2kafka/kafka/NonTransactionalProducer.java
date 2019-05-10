package cloud.thh.zk_watch2kafka.kafka;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class NonTransactionalProducer extends Producer {
  private KafkaProducer<String, byte[]> kafka;

  NonTransactionalProducer(WatchConfig config) {
    super(config);
    this.kafka = buildKafkaProducer();
  }

  @Override
  public void close() throws IOException {
    kafka.close();
  }

  @Override
  void produce(ProducerRecord<String, byte[]> record) {
    // TODO Auto-generated method stub
  }

  private KafkaProducer<String, byte[]> buildKafkaProducer() {
    Properties props = new Properties();
    props.put("bootstrap.servers", getConfig().kafka);
    props.put("enable.idempotence", getConfig().enableIdempotence);

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
