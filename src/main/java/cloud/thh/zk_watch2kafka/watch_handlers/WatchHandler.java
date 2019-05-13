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

package cloud.thh.zk_watch2kafka.watch_handlers;

import java.io.Closeable;
import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.kafka.UnrecoverableKafkaException;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;
import cloud.thh.zk_watch2kafka.zookeeper.ZnodeWatcher;

public abstract class WatchHandler implements Closeable {
  protected WatchConfig config;
  protected ZooKeeper zkClient;
  private Producer producer;

  WatchHandler(WatchConfig config, ZooKeeper zkClient, Producer producer) {
    this.config = config;
    this.zkClient = zkClient;
    this.producer = producer;
  }

  public static WatchHandler build(WatchConfig config) throws IOException {
    ZnodeWatcher znodeWatcher = new ZnodeWatcher();
    WatchHandler handler = build(
        config,
        buildZkClient(config, znodeWatcher),
        Producer.buildProducer(config));
    znodeWatcher.setHandler(handler);
    return handler;
  }

  static WatchHandler build(WatchConfig config,
      ZooKeeper zkClient, Producer producer) {
    switch(config.operation) {
      case GET_CHILDREN:
        return new GetChildrenHandler(config, zkClient, producer);
      case GET_DATA:
        return new GetDataHandler(config, zkClient, producer);
      default:
        throw new RuntimeException("Unrecognized operation");
    }
  }

  @Override
  public void close() throws IOException {
    try {
      zkClient.close();
    } catch (InterruptedException e) {
      throw new IOException(e);
    }
    producer.close();
  }

  public void handle(WatchedEvent watchedEvent) {
    try {
      ZkEvent zkEvent = watch();
      producer.produce(config.znode, zkEvent);
    } catch (UnrecoverableZkException | UnrecoverableKafkaException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Starts watching the specified node and sends the corresponding events to
   * Kafka.
   */
  public abstract ZkEvent watch() throws UnrecoverableZkException;

  private static ZooKeeper buildZkClient(
      WatchConfig config,
      ZnodeWatcher znodeWatcher) throws IOException {
    return new ZooKeeper(
        config.zookeeper,
        60000, // TODO: provide timeout through configuration
        znodeWatcher);
  }
}
