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

package cloud.thh.zk_watch2kafka.config;

public class WatchConfig {
  public enum Operation { GET_DATA, GET_CHILDREN }

  public String zookeeperId;
  public String zookeeper;
  public String znode;
  public Operation operation;

  public String kafka;
  public String targetTopic;

  public String acks;
  public boolean enableIdempotence;
  public int retries;

  public String serializer;
}
