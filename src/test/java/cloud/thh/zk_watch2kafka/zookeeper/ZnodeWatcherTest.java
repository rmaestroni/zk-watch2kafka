package cloud.thh.zk_watch2kafka.zookeeper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.WatchHandler;

public class ZnodeWatcherTest {
  @Test
  public void processDelegatesToHandler() {
    WatchHandler handler = mock(WatchHandler.class);
    WatchedEvent sampleEv = mock(WatchedEvent.class);

    ZnodeWatcher watcher = new ZnodeWatcher(handler);
    watcher.process(sampleEv);

    verify(handler, times(1)).handle(sampleEv);
  }
}
