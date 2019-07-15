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

package cloud.thh.zk_watch2kafka.kafka.serializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public abstract class XsonSerializerSharedTest {
  @Test
  public void serializeGetChildrenEvent() {
    List<String> znodeChildren = Arrays.asList("foo", "bar");
    ZkEvent event = ZkEvent.buildGetChildrenEvent(znodeChildren);
    ZkEventSerializer serializer = getSerializer();

    Map<String, ?> json = parse(serializer.serialize(null, event));
    serializer.close();

    assertEquals(znodeChildren, json.get("children"));
    assertNull(json.get("data"));
  }

  @Test
  public void serializeGetDataEvent() {
    byte[] znodeData = new byte[] { -1 };
    Stat stat = new Stat();
    stat.setDataLength(1);
    stat.setNumChildren(0);
    stat.setVersion(200);

    ZkEvent event = ZkEvent.buildGetDataEvent(znodeData, stat);
    ZkEventSerializer serializer = getSerializer();

    Map<String, ?> json = parse(serializer.serialize(null, event));
    serializer.close();

    assertNull(json.get("children"));
    assertNotNull(json.get("data"));
    assertEquals(stat.getDataLength(), json.get("dataLength"));
    assertEquals(stat.getNumChildren(), json.get("numChildren"));
    assertEquals(stat.getVersion(), json.get("version"));
  }

  protected abstract JsonFactory getFactory();

  protected abstract ZkEventSerializer getSerializer();

  @SuppressWarnings("unchecked")
  protected Map<String, ?> parse(byte[] binary) {
    ObjectMapper mapper = new ObjectMapper(getFactory());
    try {
      return mapper.readValue(binary, Map.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
