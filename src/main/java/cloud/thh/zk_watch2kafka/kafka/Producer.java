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

import java.io.Closeable;
import java.io.IOException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public abstract class Producer implements Closeable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

  private WatchConfig config;

  public static Producer buildProducer(WatchConfig config) {
    return new NonTransactionalProducer(config);
  }

  /**
   * Loads and instantiates the serializer defined in the configuration for
   * Kafka message values.
   * @return a serializer implementing {@link ZkEventSerializer}
   */
  static ZkEventSerializer buildSerializer(WatchConfig config) {
    try {
      @SuppressWarnings("unchecked")
      Class<ZkEventSerializer> serClass =
        (Class<ZkEventSerializer>) Class.forName(config.serializer);
      return serClass.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  protected Producer(WatchConfig config) {
    this.config = config;
  }

  @Override
  public void close() throws IOException {
    try {
      LOGGER.debug("Closing producer");
      getKafkaProducer().close();
    } catch (KafkaException e) {
      throw new IOException(e);
    }
  }

  /**
   * Sends the provided event to Kafka.
   */
  public void produce(String key, ZkEvent value) throws UnrecoverableKafkaException {
    produce(buildRecord(key, value));
  }

  ProducerRecord<String, ZkEvent> buildRecord(String key, ZkEvent value) {
    return new ProducerRecord<>(config.targetTopic, key, value);
  }

  abstract void produce(ProducerRecord<String, ZkEvent> record) throws UnrecoverableKafkaException;

  protected WatchConfig getConfig() {
    return config;
  }

  protected abstract KafkaProducer<?, ?> getKafkaProducer();
}
