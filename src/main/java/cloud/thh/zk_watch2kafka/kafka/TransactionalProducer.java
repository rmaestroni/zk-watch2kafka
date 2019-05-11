package cloud.thh.zk_watch2kafka.kafka;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class TransactionalProducer extends Producer {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalProducer.class);

  private KafkaProducer<String, byte[]> producer;
  private boolean initDone = false;

  TransactionalProducer(WatchConfig config) {
    this(config, buildKafkaProducer(config));
  }

  TransactionalProducer(WatchConfig config, KafkaProducer<String, byte[]> kafka) {
    super(config);
    this.producer = kafka;
  }

  @Override
  public void close() throws IOException {
    try {
      LOGGER.debug("Closing producer");
      producer.close();
    } catch (KafkaException e) {
      throw new IOException(e);
    }
  }

  @Override
  void produce(ProducerRecord<String, byte[]> record) throws UnrecoverableKafkaException {
    for (int retryCnt = 0;;) {
      try {
        if (!initDone) {
          LOGGER.debug("Doing initTransactions()");
          producer.initTransactions();
          initDone = true;
        }

        LOGGER.debug("Beginning transaction");
        producer.beginTransaction();

        logRecord(record);
        producer.send(record, buildLoggingCallback(record));

        producer.commitTransaction();
        LOGGER.debug("Transaction successfully committed");

        break;

      } catch (ProducerFencedException | OutOfOrderSequenceException |
          AuthorizationException | UnsupportedVersionException e) {
        // We can't recover from these exceptions
        LOGGER.error("Got unrecoverable error, re-throwing", e);
        throw new UnrecoverableKafkaException(e);

      } catch (KafkaException e) {
        // For all other exceptions, just abort the transaction and try again.
        if (++retryCnt < getConfig().maxTransactionRetries) {
          LOGGER.error("Aborting transaction and retrying due to recoverable error", e);
          producer.abortTransaction();
        } else {
          LOGGER.error("Retrials exausted, re-throwing unrecoverable error", e);
          throw new UnrecoverableKafkaException(e);
        }
      }
    }
  }

  private Callback buildLoggingCallback(ProducerRecord<String, byte[]> record) {
    return new Callback() {
      @Override
      public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (!LOGGER.isDebugEnabled() || null != exception) { return; }
        String msg = String.format(
            "Written record key=%s ts=%d to partition %d, offset %d",
            record.key(),
            record.timestamp(),
            metadata.partition(),
            metadata.offset());
        LOGGER.debug(msg);
      }
    };
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
    props.put("transactional.id", config.transactionalId);

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
