package cloud.thh.zk_watch2kafka.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

class ZkEventSerializer implements Serializer<ZkEvent> {
  @Override
  public void close() {
    // TODO Auto-generated method stub
  }

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @Override
  public byte[] serialize(String topic, ZkEvent data) {
    // TODO Auto-generated method stub
    return null;
  }
}
