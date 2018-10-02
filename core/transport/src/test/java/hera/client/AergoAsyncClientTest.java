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

public class AergoAsyncClientTest extends AbstractTestCase {

  protected ManagedChannel channel;

  @Before
  public void setUp() {
    this.channel = mock(ManagedChannel.class);
    when(channel.shutdown()).thenReturn(channel);
  }

  @Test
  public void testGetSignAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getSignAsyncOperation());
    }
  }

  @Test
  public void testGetAccountAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getAccountAsyncOperation());
    }
  }

  @Test
  public void testGetTransactionAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getTransactionAsyncOperation());
    }
  }

  @Test
  public void testGetBlockAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getBlockAsyncOperation());
    }
  }

  @Test
  public void testGetBlockChainAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getBlockChainAsyncOperation());
    }
  }

  @Test
  public void testGetContractAsyncOperation() throws IOException {
    try (final AergoAsyncClient client = new AergoAsyncClient(channel)) {
      assertNotNull(client.getContractAsyncOperation());
    }
  }

}
