package cloud.thh.zk_watch2kafka.watch_handlers;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

class GetChildrenHandler extends WatchHandler {
  GetChildrenHandler(WatchConfig config, ZooKeeper zkClient,
      Producer producer) {
    super(config, zkClient, producer);
  }

  @Override
  public ZkEvent watch() throws UnrecoverableZkException {
    try {
      List<String> children = zkClient.getChildren(config.znode, true);
      return ZkEvent.buildGetChildrenEvent(children);
    } catch (KeeperException | InterruptedException e) {
      throw new UnrecoverableZkException(e);
    }
  }
}
