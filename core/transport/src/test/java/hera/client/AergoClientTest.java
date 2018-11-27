/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.ContextHolder;
import org.junit.Test;

public class AergoClientTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
    ContextHolder.set(context);
  }

  @Test
  public void testGetAccountOperation() {
    try (final AergoClient client = new AergoClient()) {
      assertNotNull(client.getAccountOperation());
    }
  }

  @Test
  public void testGetBlockOperation() {
    try (final AergoClient client = new AergoClient()) {
      assertNotNull(client.getBlockOperation());
    }
  }

  @Test
  public void testGetBlockchainOperation() {
    try (final AergoClient client = new AergoClient()) {
      assertNotNull(client.getBlockchainOperation());
    }
  }

  @Test
  public void testGetTransactionOperation() {
    try (final AergoClient client = new AergoClient()) {
      assertNotNull(client.getTransactionOperation());
    }
  }

  @Test
  public void testGetContractOperation() {
    try (final AergoClient client = new AergoClient()) {
      assertNotNull(client.getContractOperation());
    }
  }

}
