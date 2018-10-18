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
import types.Node;

public class PeerConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<Peer, Node.PeerAddress> converter = new PeerConverterFactory().create();

    final Peer domainPeerAddress = new Peer();
    domainPeerAddress.setAddress(InetAddress.getByName("localhost"));
    domainPeerAddress.setPeerId(Base58Utils.encode(randomUUID().toString().getBytes()));
    final Node.PeerAddress rpcPeerAddress = converter.convertToRpcModel(domainPeerAddress);

    final Peer actualDomainPeerAddress = converter.convertToDomainModel(rpcPeerAddress);
    assertNotNull(actualDomainPeerAddress);
  }


}
