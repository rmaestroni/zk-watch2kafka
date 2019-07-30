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

package cloud.thh.zk_watch2kafka.watch_handlers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.kafka.Producer;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;
import cloud.thh.zk_watch2kafka.zookeeper.ZkEvent;

public class WatchHandlerTest {
  @Test
  public void closeClosesZkClientAndProducer() throws Exception {
    WatchConfig config = new WatchConfig();
    config.zookeeperId = "zkid";
    config.znode = "some-znode";

    ZooKeeper zk = mock(ZooKeeper.class);
    Producer prod = mock(Producer.class);

    WatchHandler handler = new WatchHandler(config, zk, prod) {
      @Override
      public ZkEvent watch() throws UnrecoverableZkException {
        return null;
      }
    };

    handler.close();

    verify(zk, times(1)).close();
    verify(prod, times(1)).close();
  }

  @Test
  public void handleCallsWatchAndProducer() throws Exception {
    WatchConfig config = new WatchConfig();
    config.zookeeperId = "zkid";
    config.znode = "some-znode";
    ZooKeeper zk = mock(ZooKeeper.class);
    Producer prod = mock(Producer.class);
    ZkEvent zkEvent = mock(ZkEvent.class);

    WatchHandler handler = mock(
        WatchHandler.class,
        withSettings()
        .useConstructor(config, zk, prod)
        .defaultAnswer(CALLS_REAL_METHODS));
    when(handler.watch()).thenReturn(zkEvent);

    handler.handle(null);

    verify(handler, times(1)).watch();
    verify(prod, times(1)).produce("zkid:some-znode", zkEvent);
  }

  @Test
  public void buildWithOperationGetChildren() {
    WatchConfig config = new WatchConfig();
    config.operation = WatchConfig.Operation.GET_CHILDREN;

    WatchHandler handler = WatchHandler.build(config, null, null);
    assertTrue(handler instanceof GetChildrenHandler);
  }

  @Test
  public void buildWithOperationGetData() {
    WatchConfig config = new WatchConfig();
    config.operation = WatchConfig.Operation.GET_DATA;

    WatchHandler handler = WatchHandler.build(config, null, null);
    assertTrue(handler instanceof GetDataHandler);
  }
}
