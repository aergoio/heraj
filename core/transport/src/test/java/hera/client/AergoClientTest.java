/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import java.io.IOException;
import org.junit.Test;

public class AergoClientTest extends AbstractTestCase {

  protected Context context = AergoClientBuilder.getDefaultContext();

  @Test
  public void testGetAccountOperation() throws IOException {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getAccountOperation());
    }
  }

  @Test
  public void testGetTransactionOperation() throws IOException {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getTransactionOperation());
    }
  }

  @Test
  public void testGetBlockOperation() throws IOException {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getBlockOperation());
    }
  }

  @Test
  public void testGetBlockChainOperation() throws IOException {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getBlockChainOperation());
    }
  }

  @Test
  public void testGetContractOperation() throws IOException {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getContractOperation());
    }
  }

}
