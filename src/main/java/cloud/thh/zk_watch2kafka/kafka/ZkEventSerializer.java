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

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public abstract class ZkEventSerializer implements Serializer<ZkEvent> {
  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public byte[] serialize(String topic, ZkEvent data) {
    // TODO Auto-generated method stub
    return null;
  }

  public abstract void setChildrenList(List<String> znodeChildren);

  public abstract void setZnodeData(byte[] data);

  public abstract void setZnodeStat(Stat stat);
}
