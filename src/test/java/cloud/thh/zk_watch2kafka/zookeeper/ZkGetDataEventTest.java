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

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

public class ZkGetDataEventTest {
  @Test
  public void initKafkaSerializer() {
    byte[] data = new byte[] { -1 };
    Stat stat = mock(Stat.class);
    ZkEvent event = new ZkGetDataEvent(data, stat);
    ZkEventSerializer serializer = mock(ZkEventSerializer.class);

    event.initKafkaSerializer(serializer);

    verify(serializer, times(1)).setZnodeData(eq(data));
    verify(serializer, times(1)).setZnodeStat(eq(stat));
  }

  @Test
  public void isEventNullReturnsFalse() {
    ZkEvent event = new ZkGetDataEvent(null, null);
    assertFalse(event.isEventNull());
  }
}
