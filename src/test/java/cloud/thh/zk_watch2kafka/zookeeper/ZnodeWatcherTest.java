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

package cloud.thh.zk_watch2kafka.zookeeper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.zookeeper.WatchedEvent;
import org.junit.Test;

import cloud.thh.zk_watch2kafka.watch_handlers.WatchHandler;

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
