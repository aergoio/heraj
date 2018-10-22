/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import org.junit.Test;

public class AergoAsyncClientTest extends AbstractTestCase {

  protected Context context = AergoClientBuilder.getDefaultContext();

  @Test
  public void testGetAccountAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getAccountAsyncOperation());
    }
  }

  @Test
  public void testGetBlockAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getBlockAsyncOperation());
    }
  }

  @Test
  public void testGetBlockchainAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getBlockchainAsyncOperation());
    }
  }

  @Test
  public void testGetTransactionAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getTransactionAsyncOperation());
    }
  }

  @Test
  public void testGetContractAsyncOperation() {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getContractAsyncOperation());
    }
  }

}
