/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class AergoEitherClientTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetAccountEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient(context)) {
      assertNotNull(client.getAccountEitherOperation());
    }
  }

  @Test
  public void testGetBlockEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient(context)) {
      assertNotNull(client.getBlockEitherOperation());
    }
  }

  @Test
  public void testGetBlockchainEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient(context)) {
      assertNotNull(client.getBlockchainEitherOperation());
    }
  }

  @Test
  public void testGetTransactionEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient(context)) {
      assertNotNull(client.getTransactionEitherOperation());
    }
  }

  @Test
  public void testGetContractEitherOperation() {
    try (final AergoEitherClient client = new AergoEitherClient(context)) {
      assertNotNull(client.getContractEitherOperation());
    }
  }

}
