/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Peer;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Rpc;

public class PeerConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<Peer, Rpc.Peer> converter = new PeerConverterFactory().create();

    final Rpc.Peer rpcPeer = Rpc.Peer.newBuilder().build();
    final Peer converted = converter.convertToDomainModel(rpcPeer);
    assertNotNull(converted);
  }

}
