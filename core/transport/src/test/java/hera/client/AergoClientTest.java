/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import io.grpc.ManagedChannel;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class AergoClientTest extends AbstractTestCase {

  protected ManagedChannel channel;

  @Before
  public void setUp() {
    this.channel = mock(ManagedChannel.class);
    when(channel.shutdown()).thenReturn(channel);
  }

  @Test
  public void testGetSignOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getSignOperation());
    }
  }

  @Test
  public void testGetAccountOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getAccountOperation());
    }
  }

  @Test
  public void testGetTransactionOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getTransactionOperation());
    }
  }

  @Test
  public void testGetBlockOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getBlockOperation());
    }
  }

  @Test
  public void testGetBlockChainOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getBlockChainOperation());
    }
  }

  @Test
  public void testGetContractOperation() throws IOException {
    try (final AergoClient client = new AergoClient(channel)) {
      assertNotNull(client.getContractOperation());
    }
  }

}
