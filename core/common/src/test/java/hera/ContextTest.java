/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import hera.api.AergoApi;
import hera.strategy.ApiStrategy;
import hera.strategy.ConnectStrategy;
import org.junit.Test;

public class ContextTest extends AbstractTestCase {

  @Test
  public void testAddStrategyGetStrategy() {
    final Context context = new Context();
    assertFalse(context.getStrategy(ConnectStrategy.class).isPresent());
    context.addStrategy((ConnectStrategy<Object>) () -> null);
    assertTrue(context.getStrategy(ConnectStrategy.class).isPresent());
  }

  @Test
  public void testApi() {
    final Context context = new Context()
        .addStrategy((ApiStrategy) () -> mock(AergoApi.class));
    final AergoApi aergoApi = context.api();
    assertNotNull(aergoApi);
  }
}