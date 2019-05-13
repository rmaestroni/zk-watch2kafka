package cloud.thh.zk_watch2kafka.watch_handlers;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

class GetDataHandler extends WatchHandler {
  GetDataHandler(WatchConfig config, ZooKeeper zkClient,
      Producer producer) {
    super(config, zkClient, producer);
  }

  @Override
  public ZkEvent watch() throws UnrecoverableZkException {
    Stat stat = new Stat();
    try {
      byte[] data = zkClient.getData(config.znode, true, stat);
      return ZkEvent.buildGetDataEvent(data, stat);
    } catch (KeeperException | InterruptedException e) {
      throw new UnrecoverableZkException(e);
    }
  }
}
