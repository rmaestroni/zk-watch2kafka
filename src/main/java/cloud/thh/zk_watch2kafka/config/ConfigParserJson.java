package cloud.thh.zk_watch2kafka.config;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigParserJson {
  private JsonNode jsonRoot;

  public ConfigParserJson(String jsonConf) throws InvalidConfigException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      jsonRoot = mapper.readTree(jsonConf);
    } catch (IOException e) {
      throw new InvalidConfigException(e);
    }
    if (!ConfigValidator.isValid(jsonRoot)) {
      throw new InvalidConfigException("Invalid config file");
    }
  }

  public List<WatchConfig> getWatches() {
    Iterator<JsonNode> it = jsonRoot.path("watches").elements();
    Iterable<JsonNode> iterable = () -> it;
    return StreamSupport
      .stream(iterable.spliterator(), false)
      .map(jsonNode -> {
        WatchConfig conf = new WatchConfig();
        conf.zookeeper = jsonNode.path("zookeeper").asText();
        conf.kafka = jsonNode.path("kafka").asText();
        conf.znode = jsonNode.path("znode").asText();
        conf.targetTopic = jsonNode.path("target_topic").asText();

        return conf;
      })
      .collect(Collectors.toList());
  }
}
