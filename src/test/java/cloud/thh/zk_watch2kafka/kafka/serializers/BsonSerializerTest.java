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

import java.util.Map;

import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;
import de.undercouch.bson4jackson.BsonFactory;

public class BsonSerializerTest extends XsonSerializerSharedTest {
  private JsonFactory factory = new BsonFactory();

  @Test
  public void serializeMtimeAndCtime() {
    byte[] znodeData = new byte[] { -1 };
    Stat stat = new Stat();
    stat.setCtime(100);
    stat.setMtime(101);

    ZkEvent event = ZkEvent.buildGetDataEvent(znodeData, stat);
    ZkEventSerializer serializer = getSerializer();

    Map<String, ?> json = parse(serializer.serialize(null, event));
    serializer.close();

    assertEquals(stat.getCtime(), json.get("ctime"));
    assertEquals(stat.getMtime(), json.get("mtime"));
  }

  @Override
  protected ZkEventSerializer getSerializer() {
    return new BsonSerializer();
  }

  @Override
  protected JsonFactory getFactory() {
    return factory;
  }
}
