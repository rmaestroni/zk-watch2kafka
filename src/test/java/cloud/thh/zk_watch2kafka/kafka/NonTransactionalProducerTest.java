package cloud.thh.zk_watch2kafka.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.junit.Test;

public class NonTransactionalProducerTest {
  @Test
  public void closeClosesTheProducer() throws IOException {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, byte[]> kafkaProd = mock(KafkaProducer.class);

    Producer producer = new NonTransactionalProducer(null, kafkaProd);
    producer.close();

    verify(kafkaProd, times(1)).close();
  }

  @Test
  public void produceSuccessfully() throws Exception {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, byte[]> kafkaProd = mock(KafkaProducer.class);
    @SuppressWarnings("unchecked")
    Future<RecordMetadata> result = mock(Future.class);

    when(kafkaProd.send(any())).thenReturn(result);
    when(result.get()).thenReturn(new RecordMetadata(null, 0, 0, 0, 0L, 0, 0));

    @SuppressWarnings("resource")
    Producer producer = new NonTransactionalProducer(null, kafkaProd);

    producer.produce(null);
    producer.produce(null);

    verify(kafkaProd, times(2)).send(any());
  }

  @Test
  public void produceWrapsKafkaExceptionOnFailure() throws Exception {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, byte[]> kafkaProd = mock(KafkaProducer.class);
    @SuppressWarnings("unchecked")
    Future<RecordMetadata> result = mock(Future.class);

    when(kafkaProd.send(any())).thenReturn(result);
    when(result.get()).thenThrow(new KafkaException("foo"));

    @SuppressWarnings("resource")
    Producer producer = new NonTransactionalProducer(null, kafkaProd);

    try {
      producer.produce(null);
      fail("Should have failed");
    } catch (UnrecoverableKafkaException e) {
      assertEquals(KafkaException.class, e.getCause().getClass());
    }
  }
}
