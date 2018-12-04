/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.AergoApi;
import hera.api.AergoAsyncApi;
import hera.api.AergoEitherApi;
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

  @Test
  public void testBuildEither() {
    final AergoEitherApi aergoEitherApi = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .buildEither();
    assertNotNull(aergoEitherApi);
  }

  @Test
  public void testBuildAsync() {
    final AergoAsyncApi aergoAsyncApi = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .buildAsync();
    assertNotNull(aergoAsyncApi);
  }

}
