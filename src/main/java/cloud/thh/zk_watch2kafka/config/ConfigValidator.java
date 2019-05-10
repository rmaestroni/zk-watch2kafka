package cloud.thh.zk_watch2kafka.config;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

class ConfigValidator {
  private static JsonSchema SCHEMA;

  static {
    InputStream schemaFile = Thread
        .currentThread()
        .getContextClassLoader()
        .getResourceAsStream("cloud/thh/zk-watch2kafka/config_json_schema.json");
    try {
      JsonNode schemaParsed = new ObjectMapper().readTree(schemaFile);
      SCHEMA = JsonSchemaFactory.byDefault().getJsonSchema(schemaParsed);
      schemaFile.close();
    } catch (IOException | ProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  static boolean isValid(JsonNode config) {
    try {
      return SCHEMA.validInstance(config);
    } catch (ProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
