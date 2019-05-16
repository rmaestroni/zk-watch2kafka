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

package cloud.thh.zk_watch2kafka.zookeeper;

import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

class ZkGetDataEvent extends ZkEvent {
  private byte[] data;
  private Stat stat;

  ZkGetDataEvent(byte[] data, Stat stat) {
    this.data = data;
    this.stat = stat;
  }

  @Override
  public void initKafkaSerializer(ZkEventSerializer serializer) {
    serializer.setZnodeData(data);
    serializer.setZnodeStat(stat);
  }
}
