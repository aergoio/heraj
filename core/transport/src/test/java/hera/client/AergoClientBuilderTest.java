/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class AergoClientBuilderTest extends AbstractTestCase {

  @Test
  public void testBuild() {
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withPlainText()
        .withNonBlockingConnect()
        .withTimeout(3000L, TimeUnit.MILLISECONDS)
        .withRetry(3, 1000, TimeUnit.MILLISECONDS)
        .build();
    assertNotNull(aergoClient);
  }

}
