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
        conf.operation = WatchConfig.Operation.valueOf(jsonNode.path("operation").asText());
        conf.targetTopic = jsonNode.path("target_topic").asText();
        conf.transactionalId = jsonNode.path("transactional_id").asText();
        conf.enableIdempotence = jsonNode.path("enable_idempotence").asBoolean();
        conf.maxTransactionRetries = jsonNode.path("max_transaction_retries").asInt();
        conf.acks = jsonNode.path("acks").asText();
        conf.retries = jsonNode.path("retries").asInt();
        conf.serializer = jsonNode.path("serializer").asText();

        return conf;
      })
      .collect(Collectors.toList());
  }
}
