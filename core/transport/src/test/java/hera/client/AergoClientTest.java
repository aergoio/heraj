/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.Context;
import org.junit.Test;

public class AergoClientTest extends AbstractTestCase {

  protected Context context = AergoClientBuilder.getDefaultContext();

  @Test
  public void testGetAccountOperation() {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getAccountOperation());
    }
  }

  @Test
  public void testGetBlockOperation() {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getBlockOperation());
    }
  }

  @Test
  public void testGetBlockchainOperation() {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getBlockchainOperation());
    }
  }

  @Test
  public void testGetTransactionOperation() {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getTransactionOperation());
    }
  }

  @Test
  public void testGetContractOperation() {
    try (final AergoClient client = new AergoClient(context)) {
      assertNotNull(client.getContractOperation());
    }
  }

}
