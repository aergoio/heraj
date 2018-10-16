/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class RemoteSignStrategyTest extends AbstractTestCase {

  @Test
  public void testGetOperation() throws InterruptedException {
    final SignStrategy<ManagedChannel> signStrategy = new RemoteSignStrategy();
    assertNotNull(signStrategy.getSignOperation());
  }

}
