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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.zookeeper.UnrecoverableZkException;

public class GetChildrenHandlerTest {
  @Test
  public void watch() throws Exception {
    WatchConfig config = new WatchConfig();
    config.znode = "some-znode";
    ZooKeeper zk = mock(ZooKeeper.class);

    @SuppressWarnings("resource")
    GetChildrenHandler handler = new GetChildrenHandler(config, zk, null);
    handler.watch();

    verify(zk, times(1)).getChildren("some-znode", true);
  }

  @Test(expected=UnrecoverableZkException.class)
  public void watchOnFailureWrapsException() throws Exception {
    WatchConfig config = new WatchConfig();
    config.znode = "some-znode";
    ZooKeeper zk = mock(ZooKeeper.class);
    when(zk.getChildren(eq("some-znode"), eq(true))).thenThrow(mock(KeeperException.class));

    @SuppressWarnings("resource")
    GetChildrenHandler handler = new GetChildrenHandler(config, zk, null);
    handler.watch();
  }
}
