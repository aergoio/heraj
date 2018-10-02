/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import hera.Context;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class AergoEitherClientApiStrategyTest extends AbstractTestCase {

  @Test
  public void testGetApi() {
    final Context context = new Context();
    context.addStrategy((ConnectStrategy) () -> mock(ManagedChannel.class));
    final AergoEitherClientApiStrategy apiStrategy = new AergoEitherClientApiStrategy();
    apiStrategy.setContext(context);
    apiStrategy.getEitherApi();

  }

}