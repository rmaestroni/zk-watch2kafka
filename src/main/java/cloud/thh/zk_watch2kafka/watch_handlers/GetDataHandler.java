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

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

class GetDataHandler extends WatchHandler {
  GetDataHandler(WatchConfig config, ZooKeeper zkClient,
      Producer producer) {
    super(config, zkClient, producer);
  }

  @Override
  public ZkEvent watch() throws UnrecoverableZkException {
    Stat stat = new Stat();
    try {
      byte[] data = zkClient.getData(config.znode, true, stat);
      return ZkEvent.buildGetDataEvent(data, stat);
    } catch (KeeperException | InterruptedException e) {
      throw new UnrecoverableZkException(e);
    }
  }
}
