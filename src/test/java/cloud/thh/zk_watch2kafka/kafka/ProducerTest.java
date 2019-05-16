package cloud.thh.zk_watch2kafka.kafka;

import static org.junit.Assert.fail;

import org.junit.Test;

import cloud.thh.zk_watch2kafka.config.WatchConfig;

public class ProducerTest {

  @Test
  public void buildSerializer() {
    WatchConfig config = new WatchConfig();
    config.serializer = "java.lang.Object"; // TODO: complete this test with a serializer class

    fail("Not implemented yet");
  }
}
