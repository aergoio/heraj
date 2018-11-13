package hera.strategy;

import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import org.junit.Test;

public class ZipkinTracingStrategyTest {

  @Test
  public void testConfigure() {

    final Configuration configuration = new InMemoryConfiguration();
    configuration.define("endpoint", "localhost:7845");

    final AergoClient aergoClient = new AergoClientBuilder()
        .setConfiguration(configuration)
        .addStrategy(new ZipkinTracingStrategy())
        .addStrategy(new NettyConnectStrategy())
        .build();
  }

}