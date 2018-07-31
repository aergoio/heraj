/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.AbstractTestCase;
import hera.Context;
import org.junit.Test;

public class AergoClientApiStrategyTest extends AbstractTestCase {

  @Test
  public void testGetApi() {
    final Context context = new Context();
    context.addStrategy((ConnectStrategy) () -> null);
    final AergoClientApiStrategy apiStrategy = new AergoClientApiStrategy();
    apiStrategy.setContext(context);
    apiStrategy.getApi();

  }

}