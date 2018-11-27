/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.ContextHolder;
import org.junit.Test;

public class AergoAsyncClientTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
    ContextHolder.set(context);
  }

  @Test
  public void testGetAccountAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient()) {
      assertNotNull(client.getAccountAsyncOperation());
    }
  }

  @Test
  public void testGetBlockAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient()) {
      assertNotNull(client.getBlockAsyncOperation());
    }
  }

  @Test
  public void testGetBlockchainAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient()) {
      assertNotNull(client.getBlockchainAsyncOperation());
    }
  }

  @Test
  public void testGetTransactionAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient()) {
      assertNotNull(client.getTransactionAsyncOperation());
    }
  }

  @Test
  public void testGetContractAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient()) {
      assertNotNull(client.getContractAsyncOperation());
    }
  }

}
