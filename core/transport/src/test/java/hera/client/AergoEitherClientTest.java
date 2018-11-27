/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.ContextHolder;
import org.junit.Test;

public class AergoEitherClientTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
    ContextHolder.set(context);
  }

  @Test
  public void testGetAccountEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient()) {
      assertNotNull(client.getAccountEitherOperation());
    }
  }

  @Test
  public void testGetBlockEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient()) {
      assertNotNull(client.getBlockEitherOperation());
    }
  }

  @Test
  public void testGetBlockchainEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient()) {
      assertNotNull(client.getBlockchainEitherOperation());
    }
  }

  @Test
  public void testGetTransactionEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient()) {
      assertNotNull(client.getTransactionEitherOperation());
    }
  }

  @Test
  public void testGetContractEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient()) {
      assertNotNull(client.getContractEitherOperation());
    }
  }

}
