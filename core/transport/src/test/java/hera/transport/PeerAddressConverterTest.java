/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.PeerAddress;
import org.junit.Test;
import types.Node;

public class PeerAddressConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<PeerAddress, Node.PeerAddress> converter = new PeerAddressConverterFactory()
        .create();

    final PeerAddress domainPeerAddress = new PeerAddress();
    final Node.PeerAddress rpcPeerAddress = converter.convertToRpcModel(domainPeerAddress);
    final PeerAddress actualDomainPeerAddress = converter.convertToDomainModel(rpcPeerAddress);
    assertNotNull(actualDomainPeerAddress);
  }


}
