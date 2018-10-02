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
import org.junit.Test;

public class AergoEitherClientTest extends AbstractTestCase {

  @Test
  public void testGetAccountOperation() throws IOException {
    final ManagedChannel channel = mock(ManagedChannel.class);
    when(channel.shutdown()).thenReturn(channel);
    try(final AergoEitherClient client = new AergoEitherClient(channel)) {
      assertNotNull(client.getAccountOperation());
    }

  }

}