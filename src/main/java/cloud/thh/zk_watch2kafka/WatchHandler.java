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

package cloud.thh.zk_watch2kafka;

import java.io.Closeable;
import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZnodeWatcher;

public class WatchHandler implements Closeable {
  private WatchConfig config;
  private ZooKeeper zkClient;
  private Producer producer;

  public WatchHandler(WatchConfig config) throws IOException {
    this.config = config;
    this.zkClient = buildZkClient();
    this.producer = Producer.buildProducer(config);
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

  public void handle(WatchedEvent event) {
    // TODO
  }

  /**
   * Starts watching the specified node and sends the corresponding events to
   * Kafka.
   */
  public void watch() throws UnrecoverableZkException {
    try {
      zkClient.getChildren(config.znode, true);
    } catch (KeeperException | InterruptedException e) {
      throw new UnrecoverableZkException(e);
    }
  }

  private ZooKeeper buildZkClient() throws IOException {
    return new ZooKeeper(
        config.zookeeper,
        60000, // TODO: provide timeout through configuration
        new ZnodeWatcher(this));
  }
}
