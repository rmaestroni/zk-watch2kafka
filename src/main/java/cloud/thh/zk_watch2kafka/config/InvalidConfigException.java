package cloud.thh.zk_watch2kafka.config;

public class InvalidConfigException extends Exception {
  private static final long serialVersionUID = -2563869008798268287L;

  InvalidConfigException(Throwable t) { super(t); }

  InvalidConfigException(String msg) { super(msg); }
}
