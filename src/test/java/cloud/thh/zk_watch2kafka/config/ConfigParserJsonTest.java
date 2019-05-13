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

package cloud.thh.zk_watch2kafka.config;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import cloud.thh.zk_watch2kafka.TestHelper;

public class ConfigParserJsonTest {
  @Test(expected=InvalidConfigException.class)
  public void failsWithMalformedJson() throws InvalidConfigException {
    new ConfigParserJson("invalid-json");
  }

  @Test(expected=InvalidConfigException.class)
  public void failsWithInvalidConfig() throws InvalidConfigException {
    new ConfigParserJson("{\"foo\":\"bar\"}");
  }

  @Test
  public void parsesConfig() throws InvalidConfigException {
    ConfigParserJson parser = new ConfigParserJson(
        TestHelper.readFixture("sample-config.json"));

    List<WatchConfig> confs = parser.getWatches();
    assertEquals(1, confs.size());

    WatchConfig conf = confs.get(0);

    assertEquals("localhost:2181", conf.zookeeper);
    assertEquals("xyz", conf.znode);
    assertEquals(WatchConfig.Operation.GET_DATA, conf.operation);

    assertEquals("localhost:9092", conf.kafka);
    assertEquals("zookeeper-watch-events", conf.targetTopic);
    assertEquals("some-transactional-id", conf.transactionalId);
    assertEquals(true, conf.enableIdempotence);
    assertEquals(10, conf.maxTransactionRetries);

    assertEquals("", conf.acks);
    assertEquals(-1, conf.retries);
  }
}
