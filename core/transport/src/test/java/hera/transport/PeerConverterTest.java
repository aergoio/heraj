/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Peer;
import hera.util.Base58Utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Rpc;

public class PeerConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<Peer, Rpc.Peer> converter = new PeerConverterFactory().create();

    final Peer domain = new Peer();
    domain.setAddress(InetAddress.getByName("localhost"));
    domain.setPeerId(Base58Utils.encode(randomUUID().toString().getBytes()));
    final Rpc.Peer rpcPeer = converter.convertToRpcModel(domain);

    final Peer actualDomainPeer = converter.convertToDomainModel(rpcPeer);
    assertNotNull(actualDomainPeer);
  }


}
