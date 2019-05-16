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

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public class ZkEventSerializerTest {
  @Test
  public void serialize() {
    ZkEventSerializer serializer = mock(ZkEventSerializer.class);
    ZkEvent event = mock(ZkEvent.class);
    when(serializer.serialize(any(), eq(event))).thenCallRealMethod();

    serializer.serialize(null, event);

    verify(serializer, times(1)).reset();
    verify(event, times(1)).initKafkaSerializer(eq(serializer));
    verify(serializer, times(1)).serialize();
  }

  @Test
  public void serializeWhenNoZnode() {
    ZkEventSerializer serializer = mock(ZkEventSerializer.class);
    ZkEvent event = mock(ZkEvent.class);
    when(event.isEventNull()).thenReturn(true);
    when(serializer.serialize(any(), eq(event))).thenCallRealMethod();

    byte[] data = serializer.serialize(null, event);

    assertNull(data);

    verify(event, times(1)).isEventNull();
    verify(serializer, times(1)).reset();
    verify(event, times(0)).initKafkaSerializer(eq(serializer));
    verify(serializer, times(0)).serialize();
  }
}
