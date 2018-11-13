package hera.strategy;

import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import org.junit.Test;

public class ZipkinTracingStrategyTest {

  @Test
  public void testConfigure() {
    final AergoClient aergoClient = new AergoClientBuilder()
        .withZipkinTracking()
        .build();
  }

}