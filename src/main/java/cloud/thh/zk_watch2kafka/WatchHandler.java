package cloud.thh.zk_watch2kafka;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.zookeeper.ZnodeWatcher;

public class WatchHandler {
  private WatchConfig config;
  private ZooKeeper zkClient;

  public WatchHandler(WatchConfig config) {
    this.config = config;
    this.zkClient = buildZkClient();
  }

  public void handle(WatchedEvent event) {
    // TODO
  }

  private ZooKeeper buildZkClient() {
    try {
      return new ZooKeeper(
          config.zookeeper,
          60000, // TODO: provide timeout through configuration
          new ZnodeWatcher(this));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
