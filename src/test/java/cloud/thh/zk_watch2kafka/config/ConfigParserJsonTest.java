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

    assertEquals("localhost:9092", conf.kafka);
    assertEquals("zookeeper-watch-events", conf.targetTopic);
    assertEquals("some-transactional-id", conf.transactionalId);
    assertEquals(true, conf.enableIdempotence);
  }
}
