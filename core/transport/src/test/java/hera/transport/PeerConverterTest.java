/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
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

    final Peer domain = new Peer(InetAddress.getByName("localhost"), 8080,
        Base58Utils.encode(randomUUID().toString().getBytes()),
        new BlockchainStatus(10L, new BlockHash(BytesValue.EMPTY)), 1);
    final Rpc.Peer rpcPeer = converter.convertToRpcModel(domain);

    final Peer actualDomainPeer = converter.convertToDomainModel(rpcPeer);
    assertNotNull(actualDomainPeer);
  }


}
