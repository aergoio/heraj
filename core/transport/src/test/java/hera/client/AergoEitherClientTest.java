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

public class AergoEitherClientTest extends AbstractTestCase {

  protected ManagedChannel channel;

  @Before
  public void setUp() {
    this.channel = mock(ManagedChannel.class);
    when(channel.shutdown()).thenReturn(channel);
  }

  @Test
  public void testGetSignEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getSignEitherOperation());
    }
  }

  @Test
  public void testGetAccountEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getAccountEitherOperation());
    }
  }

  @Test
  public void testGetTransactionEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getTransactionEitherOperation());
    }
  }

  @Test
  public void testGetBlockEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getBlockEitherOperation());
    }
  }

  @Test
  public void testGetBlockChainEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getBlockChainEitherOperation());
    }
  }

  @Test
  public void testGetContractEitherOperation() throws IOException {
    try (final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getContractEitherOperation());
    }
  }

}
