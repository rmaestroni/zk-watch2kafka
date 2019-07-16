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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

public class AvroSerializer extends ZkEventSerializer {
  static Schema schema;

  static {
    try {
      InputStream schemaIn = Thread
          .currentThread()
          .getContextClassLoader()
          .getResourceAsStream("cloud/thh/zk-watch2kafka/avro/zk_event.avsc");
      schema = new Schema.Parser().parse(schemaIn);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private transient BinaryEncoder binaryEncoder;
  private GenericRecord avroEvent;

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public void close() {
    avroEvent = null;
  }

  @Override
  public void setChildrenList(List<String> znodeChildren) {
    avroEvent.put("children", znodeChildren);
  }

  @Override
  public void setZnodeData(byte[] data) {
    avroEvent.put("zNodeData", ByteBuffer.wrap(data));
  }

  @Override
  public void setZnodeStat(Stat stat) {
    avroEvent.put("dataLength", stat.getDataLength());
    avroEvent.put("numChildren", stat.getNumChildren());
    avroEvent.put("ctime", stat.getCtime());
    avroEvent.put("mtime", stat.getMtime());
    avroEvent.put("version", stat.getVersion());
  }

  @Override
  protected void reset() {
    avroEvent = new GenericData.Record(schema);
  }

  @Override
  protected synchronized byte[] serialize() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    this.binaryEncoder = EncoderFactory.get().binaryEncoder(out, this.binaryEncoder);

    DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);

    try {
      datumWriter.write(avroEvent, binaryEncoder);
      this.binaryEncoder.flush();
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return out.toByteArray();
  }
}
