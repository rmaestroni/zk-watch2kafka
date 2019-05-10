package cloud.thh.zk_watch2kafka.kafka;

import org.apache.kafka.common.KafkaException;

public class UnrecoverableKafkaException extends Exception {
  private static final long serialVersionUID = -8797018753720240484L;

  public UnrecoverableKafkaException(KafkaException e) {
    super(e);
  }
}
