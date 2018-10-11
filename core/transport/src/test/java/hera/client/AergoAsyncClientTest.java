/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import java.io.IOException;
import org.junit.Test;

public class AergoAsyncClientTest extends AbstractTestCase {

  protected Context context = AergoClientBuilder.getDefaultContext();

  @Test
  public void testGetSignAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getSignAsyncOperation());
    }
  }

  @Test
  public void testGetAccountAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getAccountAsyncOperation());
    }
  }

  @Test
  public void testGetTransactionAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getTransactionAsyncOperation());
    }
  }

  @Test
  public void testGetBlockAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getBlockAsyncOperation());
    }
  }

  @Test
  public void testGetBlockChainAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getBlockChainAsyncOperation());
    }
  }

  @Test
  public void testGetContractAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(context)) {
      assertNotNull(client.getContractAsyncOperation());
    }
  }

}
