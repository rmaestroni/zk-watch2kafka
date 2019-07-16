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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public class AvroSerializerTest {
  private transient BinaryDecoder binaryDecoder;

  @Test
  public void serializeGetChildrenEvent() {
    List<String> znodeChildren = Arrays.asList("foo", "bar");
    ZkEvent event = ZkEvent.buildGetChildrenEvent(znodeChildren);
    ZkEventSerializer serializer = new AvroSerializer();

    GenericRecord record = parse(serializer.serialize(null, event));
    serializer.close();

    @SuppressWarnings("unchecked")
    GenericData.Array<Utf8> ary = (Array<Utf8>) record.get("children");
    assertEquals("foo", ary.get(0).toString());
    assertEquals("bar", ary.get(1).toString());

    assertNull(record.get("data"));
  }

  @Test
  public void serializeGetDataEvent() {
    byte[] znodeData = new byte[] { -10 };
    Stat stat = new Stat();
    stat.setDataLength(1);
    stat.setNumChildren(0);
    stat.setVersion(200);
    stat.setCtime(100);
    stat.setMtime(101);

    ZkEvent event = ZkEvent.buildGetDataEvent(znodeData, stat);
    ZkEventSerializer serializer = new AvroSerializer();

    GenericRecord record = parse(serializer.serialize(null, event));
    serializer.close();

    assertNull(record.get("children"));

    assertNotNull(record.get("zNodeData"));
    ByteBuffer deserializedZnodeData = (ByteBuffer) record.get("zNodeData");
    assertEquals(1, deserializedZnodeData.limit());
    assertEquals(-10, deserializedZnodeData.get());

    assertEquals(stat.getDataLength(), record.get("dataLength"));
    assertEquals(stat.getNumChildren(), record.get("numChildren"));
    assertEquals(stat.getVersion(), record.get("version"));
    assertEquals(stat.getCtime(), record.get("ctime"));
    assertEquals(stat.getMtime(), record.get("mtime"));
  }

  protected GenericRecord parse(byte[] binary) {
    DatumReader<GenericRecord> datumReader =
        new GenericDatumReader<GenericRecord>(AvroSerializer.schema);
    this.binaryDecoder = DecoderFactory.get().binaryDecoder(binary, this.binaryDecoder);
    try {
      return datumReader.read(null, this.binaryDecoder);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
