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

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloud.thh.zk_watch2kafka.watch_handlers.WatchHandler;

public class ZnodeWatcher implements Watcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZnodeWatcher.class);

  private WatchHandler handler;

  public ZnodeWatcher(WatchHandler handler) {
    this.handler = handler;
  }

  public ZnodeWatcher() {
    this(null);
  }

  @Override
  public void process(WatchedEvent event) {
    if (null != event) {
      LOGGER.debug("Received event: " + event);
    }
    if (null != handler) {
      handler.handle(event);
    }
  }

  public void setHandler(WatchHandler handler) {
    this.handler = handler;
  }
}
