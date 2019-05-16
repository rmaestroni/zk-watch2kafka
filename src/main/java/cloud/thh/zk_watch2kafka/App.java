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

package cloud.thh.zk_watch2kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cloud.thh.zk_watch2kafka.config.ConfigParserJson;
import cloud.thh.zk_watch2kafka.config.WatchConfig;
import cloud.thh.zk_watch2kafka.watch_handlers.WatchHandler;

public class App {
  public static void main(String[] args) throws Exception {
    List<WatchConfig> configs = parseConfig(args[0]);
    List<WatchHandler> handlers = new ArrayList<>();
    for (WatchConfig config: configs) {
      handlers.add(WatchHandler.build(config));
    }

    // attach shutdown handler to catch TERM
    Runtime.getRuntime().addShutdownHook(
        new Thread("shutdown-hook") {
          @Override
          public void run() {
            for (WatchHandler handler: handlers) {
              try {
                handler.close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          }
        });

    // start watch loop
    for (WatchHandler handler: handlers) {
      handler.handle(null);
    }
  }

  private static List<WatchConfig> parseConfig(String filePath) throws Exception {
    if (null == filePath) {
      System.err.println(
          "Config file path not provided, you have to specify" +
          "a valid configuration file path as program's first argument");
      System.exit(1);
    }
    String json = Files.readString(Paths.get(filePath));
    return new ConfigParserJson(json).getWatches();
  }
}
