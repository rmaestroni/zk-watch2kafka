package cloud.thh.zk_watch2kafka.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import cloud.thh.zk_watch2kafka.WatchHandler;

public class ZnodeWatcher implements Watcher {
  private WatchHandler handler;

  public ZnodeWatcher(WatchHandler handler) {
    this.handler = handler;
  }

  @Override
  public void process(WatchedEvent event) {
    handler.handle(event);
  }
}
