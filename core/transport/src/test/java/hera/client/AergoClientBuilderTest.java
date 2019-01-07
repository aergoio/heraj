/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.AergoApi;
import org.junit.Test;

public class AergoClientBuilderTest extends AbstractTestCase {

  @Test
  public void testBuild() {
    final AergoApi aergoApi = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();
    assertNotNull(aergoApi);
  }

}
