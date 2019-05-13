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
      RecordMetadata metadata = producer.send(record).get();
      logRecordSucceeded(record, metadata);

    } catch (InterruptedException | ExecutionException | KafkaException e) {
      LOGGER.error("Got unrecoverable error, re-throwing", e);
      throw new UnrecoverableKafkaException(e);
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
    if (config.enableIdempotence) {
      props.put("enable.idempotence", true);
    } else {
      props.put("retries", config.retries);
      props.put("acks", config.acks);
    }

    return new KafkaProducer<String, byte[]>(
        props, new StringSerializer(), new ByteArraySerializer());
  }
}
