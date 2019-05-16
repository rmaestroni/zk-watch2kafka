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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.serializers.JsonSerializer;

public class ProducerTest {
  @Test
  public void buildSerializer() {
    WatchConfig config = new WatchConfig();
    config.serializer = "cloud.thh.zk_watch2kafka.kafka.serializers.JsonSerializer";

    ZkEventSerializer serializer = Producer.buildSerializer(config);

    assertTrue(serializer instanceof JsonSerializer);
  }
}
