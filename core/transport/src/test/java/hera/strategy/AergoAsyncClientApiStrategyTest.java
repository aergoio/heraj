/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import hera.Context;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class AergoAsyncClientApiStrategyTest extends AbstractTestCase {

  @Test
  public void testGetApi() {
    final Context context = new Context();
    context.addStrategy((ConnectStrategy) () -> mock(ManagedChannel.class));
    final AergoAsyncClientApiStrategy apiStrategy = new AergoAsyncClientApiStrategy();
    apiStrategy.setContext(context);
    apiStrategy.getApi();

  }

}