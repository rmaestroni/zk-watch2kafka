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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

public class ZkGetChildrenEventTest {
  @Test
  public void initKafkaSerializer() {
    List<String> children = Arrays.asList("foo", "bar");
    ZkEvent event = new ZkGetChildrenEvent(children);
    ZkEventSerializer serializer = mock(ZkEventSerializer.class);

    event.initKafkaSerializer(serializer);

    verify(serializer, times(1)).setChildrenList(eq(children));
  }

  @Test
  public void isEventNullReturnsFalse() {
    ZkEvent event = new ZkGetChildrenEvent(null);
    assertFalse(event.isEventNull());
  }
}
