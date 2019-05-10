package cloud.thh.zk_watch2kafka.config;

public class WatchConfig {
  public String zookeeper;
  public String znode;

  public String kafka;
  public String targetTopic;
  public String transactionalId;
  public boolean enableIdempotence;
  public int maxTransactionRetries;
}
