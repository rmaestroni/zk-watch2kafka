package cloud.thh.zk_watch2kafka.zookeeper;

import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.kafka.ZkEventSerializer;

class ZkGetDataEvent extends ZkEvent {
  private byte[] data;
  private Stat stat;

  ZkGetDataEvent(byte[] data, Stat stat) {
    this.data = data;
    this.stat = stat;
  }

  @Override
  public void initKafkaSerializer(ZkEventSerializer serializer) {
    serializer.setZnodeData(data);
    serializer.setZnodeStat(stat);
  }
}
