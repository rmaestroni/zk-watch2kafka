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
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.data.Stat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

public class JsonSerializer extends ZkEventSerializer {
  private ByteArrayOutputStream binaryOut = new ByteArrayOutputStream();
  private JsonFactory jsonFactory = new ObjectMapper().getFactory();
  private JsonGenerator jsonGen;

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public void close() {
    try {
      binaryOut.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setChildrenList(List<String> znodeChildren) {
    try {
      jsonGen.writeArrayFieldStart("children");
      for (String child: znodeChildren) {
        jsonGen.writeString(child);
      }
      jsonGen.writeEndArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setZnodeData(byte[] data) {
    try {
      jsonGen.writeBinaryField("data", data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setZnodeStat(Stat stat) {
    try {
      jsonGen.writeNumberField("dataLength", stat.getDataLength());
      jsonGen.writeNumberField("numChildren", stat.getNumChildren());
      jsonGen.writeNumberField("ctime", stat.getCtime());
      jsonGen.writeNumberField("mtime", stat.getMtime());
      jsonGen.writeNumberField("version", stat.getVersion());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void reset() {
    binaryOut.reset();
    try {
      jsonGen = jsonFactory.createGenerator(binaryOut);
      jsonGen.writeStartObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected byte[] serialize() {
    try {
      jsonGen.writeEndObject();
      jsonGen.close();
      binaryOut.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return binaryOut.toByteArray();
  }
}
