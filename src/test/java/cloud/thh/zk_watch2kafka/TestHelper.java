package cloud.thh.zk_watch2kafka;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestHelper {
  public static String readFixture(String fixtureName) {
    try {
      return new String(
          Files.readAllBytes(
              Paths.get("src/test/fixtures/" + fixtureName)),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
