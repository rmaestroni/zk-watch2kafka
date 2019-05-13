/*
 *  Copyright (C) 2019 Roberto Maestroni
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cloud.thh.zk_watch2kafka.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public class TransactionalProducerTest {
  @Test
  public void closeClosesTheProducer() throws IOException {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, ZkEvent> kafkaProd = mock(KafkaProducer.class);

    Producer producer = new TransactionalProducer(null, kafkaProd);
    producer.close();

    verify(kafkaProd, times(1)).close();
  }

  @Test
  public void produceSuccessfully() throws UnrecoverableKafkaException {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, ZkEvent> kafkaProd = mock(KafkaProducer.class);

    @SuppressWarnings("resource")
    Producer producer = new TransactionalProducer(null, kafkaProd);

    producer.produce(null);
    producer.produce(null);

    verify(kafkaProd, times(1)).initTransactions();
    verify(kafkaProd, times(2)).beginTransaction();
    verify(kafkaProd, times(2)).send(any(), any());
    verify(kafkaProd, times(2)).commitTransaction();
  }

  @Test
  public void produceWithUnrecoverableError() {
    @SuppressWarnings("unchecked")
    KafkaProducer<String, ZkEvent> kafkaProd = mock(KafkaProducer.class);
    when(kafkaProd.send(any(), any())).thenThrow(new AuthorizationException("foo"));

    @SuppressWarnings("resource")
    Producer producer = new TransactionalProducer(null, kafkaProd);
    try {
      producer.produce(null);
      fail("It should have failed");
    } catch (UnrecoverableKafkaException e) {
      assertEquals(AuthorizationException.class, e.getCause().getClass());
    }

    verify(kafkaProd, times(1)).initTransactions();
    verify(kafkaProd, times(1)).beginTransaction();
    verify(kafkaProd, times(0)).abortTransaction();
  }

  @Test
  public void produceWithRecoverableErrorRetries() {
    WatchConfig config = new WatchConfig();
    config.maxTransactionRetries = 10;

    @SuppressWarnings("unchecked")
    KafkaProducer<String, ZkEvent> kafkaProd = mock(KafkaProducer.class);
    when(kafkaProd.send(any(), any())).thenThrow(new KafkaException("foo"));

    @SuppressWarnings("resource")
    Producer producer = new TransactionalProducer(config, kafkaProd);
    try {
      producer.produce(null);
      fail("It should have failed");
    } catch (UnrecoverableKafkaException e) {
      assertEquals(KafkaException.class, e.getCause().getClass());
    }

    verify(kafkaProd, times(1)).initTransactions();
    verify(kafkaProd, times(10)).beginTransaction();
    verify(kafkaProd, times(9)).abortTransaction();
  }
}
