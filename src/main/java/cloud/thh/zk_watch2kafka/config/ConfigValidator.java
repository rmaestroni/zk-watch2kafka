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
