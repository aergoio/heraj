/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import hera.api.AergoApi;
import hera.api.AergoAsyncApi;
import hera.api.AergoEitherApi;
import hera.strategy.ConnectStrategy;
import org.junit.Test;

public class AergoClientBuilderTest extends AbstractTestCase {

  @SuppressWarnings("rawtypes")
  @Test
  public void testAddStrategy() {
    final ConnectStrategy strategy = () -> null;
    final AergoClientBuilder builder = new AergoClientBuilder().addStrategy(strategy);
    assertNotNull(builder.context.getStrategy(ConnectStrategy.class));
  }

  @Test
  public void testBind() {
    final Context context = new Context();
    final AergoClientBuilder builder = new AergoClientBuilder().bind(context);
    assertEquals(context, builder.context);
  }

  @Test
  public void testBuild() {
    final AergoApi aergoApi = new AergoClientBuilder().build();
    assertNotNull(aergoApi);
  }

  @Test
  public void testBuildEither() {
    final AergoEitherApi aergoEitherApi = new AergoClientBuilder().buildEither();
    assertNotNull(aergoEitherApi);
  }

  @Test
  public void testBuildAsync() {
    final AergoAsyncApi aergoAsyncApi = new AergoClientBuilder().buildAsync();
    assertNotNull(aergoAsyncApi);
  }

}
