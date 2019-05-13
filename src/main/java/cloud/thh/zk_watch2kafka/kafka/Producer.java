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

import org.apache.kafka.clients.producer.ProducerRecord;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public abstract class Producer implements Closeable {
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

  public void produce(String key, byte[] value) throws UnrecoverableKafkaException {
    produce(buildRecord(key, value));
  }

  ProducerRecord<String, byte[]> buildRecord(String key, byte[] value) {
    return new ProducerRecord<>(config.targetTopic, key, value);
  }

  abstract void produce(ProducerRecord<String, byte[]> record) throws UnrecoverableKafkaException;

  protected WatchConfig getConfig() {
    return config;
  }
}
