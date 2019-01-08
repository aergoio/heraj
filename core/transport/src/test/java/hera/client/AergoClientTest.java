/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class AergoClientTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetAccountOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getAccountOperation());
    } finally {
      client.close();
    }
  }

  @Test
  public void testGetKeyStoreOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getKeyStoreOperation());
    } finally {
      client.close();
    }
  }

  @Test
  public void testGetBlockOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getBlockOperation());
    } finally {
      client.close();
    }
  }

  @Test
  public void testGetBlockchainOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getBlockchainOperation());
    } finally {
      client.close();
    }
  }

  @Test
  public void testGetTransactionOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getTransactionOperation());
    } finally {
      client.close();
    }
  }

  @Test
  public void testGetContractOperation() {
    final AergoClient client = new AergoClient(context);
    try {
      assertNotNull(client.getContractOperation());
    } finally {
      client.close();
    }
  }

}
