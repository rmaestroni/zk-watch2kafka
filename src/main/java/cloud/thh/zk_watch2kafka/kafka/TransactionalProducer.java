package cloud.thh.zk_watch2kafka.kafka;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class TransactionalProducer extends Producer {
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
          producer.initTransactions();
          initDone = true;
        }

        producer.beginTransaction();
        producer.send(record);
        producer.commitTransaction();
        break;

      } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
        // We can't recover from these exceptions
        throw new UnrecoverableKafkaException(e);
      } catch (KafkaException e) {
        // For all other exceptions, just abort the transaction and try again.
        if (++retryCnt < getConfig().maxTransactionRetries) {
          producer.abortTransaction();
        } else {
          throw new UnrecoverableKafkaException(e);
        }
      }
    }
  }

  private static KafkaProducer<String, byte[]> buildKafkaProducer(WatchConfig config) {
    Properties props = new Properties();
    props.put("bootstrap.servers", config.kafka);
    props.put("transactional.id", config.transactionalId);

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
