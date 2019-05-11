package cloud.thh.zk_watch2kafka.kafka;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class NonTransactionalProducer extends Producer {
  private static final Logger LOGGER = LoggerFactory.getLogger(NonTransactionalProducer.class);

  private KafkaProducer<String, byte[]> producer;

  NonTransactionalProducer(WatchConfig config) {
    this(config, buildKafkaProducer(config));
  }

  NonTransactionalProducer(WatchConfig config, KafkaProducer<String, byte[]> producer) {
    super(config);
    this.producer = producer;
  }

  @Override
  public void close() throws IOException {
    producer.close();
  }

  @Override
  void produce(ProducerRecord<String, byte[]> record) throws UnrecoverableKafkaException {
    try {
      logRecord(record);
      // TODO: trying not to use .get()
      RecordMetadata metadata = producer.send(record).get();
      logRecordSucceeded(record, metadata);

    } catch (KafkaException e) {
      LOGGER.error("Got unrecoverable error, re-throwing", e);
      throw new UnrecoverableKafkaException(e);

    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private void logRecordSucceeded(ProducerRecord<String, byte[]> record, RecordMetadata metadata) {
    if (!LOGGER.isDebugEnabled()) { return; }
    String msg = String.format(
        "Written record key=%s ts=%d to partition %d, offset %d",
        record.key(),
        record.timestamp(),
        metadata.partition(),
        metadata.offset());
    LOGGER.debug(msg);
  }

  private void logRecord(ProducerRecord<String, byte[]> record) {
    if (!LOGGER.isInfoEnabled()) { return; }
    LOGGER.info(
        String.format(
            "Sending record to topic=%s with key=%s ts=%d",
            record.topic(),
            record.key(),
            record.timestamp()));
  }

  private static KafkaProducer<String, byte[]> buildKafkaProducer(WatchConfig config) {
    Properties props = new Properties();
    props.put("bootstrap.servers", config.kafka);
    props.put("enable.idempotence", config.enableIdempotence);

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
