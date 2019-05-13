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

import java.util.List;

import org.apache.zookeeper.data.Stat;

public abstract class ZkEvent {
  public static ZkEvent buildGetChildrenEvent(List<String> children) {
    // TODO
    return null;
  }

  public static ZkEvent buildGetDataEvent(byte[] data, Stat stat) {
    // TODO
    return null;
  }
}
