/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import org.junit.Test;

public class LocalSignStrategyTest extends AbstractTestCase {

  @Test
  public void testGetOperation() throws InterruptedException {
    final SignStrategy<?> signStrategy = new LocalSignStrategy();
    assertNotNull(signStrategy.getSignOperation(null, new Context()));
  }

}
