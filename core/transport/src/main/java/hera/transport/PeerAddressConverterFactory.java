/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BytesValue;
import hera.api.model.PeerAddress;
import java.util.function.Function;
import org.slf4j.Logger;

public class PeerAddressConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<PeerAddress, types.Node.PeerAddress> domainConverter =
      domainPeerAddress -> {
        logger.trace("Domain peer address: {}", domainPeerAddress);
        return types.Node.PeerAddress.newBuilder()
            .setAddress(copyFrom(domainPeerAddress.getAddress()))
            .setPort(domainPeerAddress.getPort())
            .setPeerID(copyFrom(domainPeerAddress.getPeerId()))
            .build();
      };

  protected final Function<types.Node.PeerAddress, PeerAddress> rpcConverter =
      rpcStatus -> {
        logger.trace("Rpc peer address: {}", rpcStatus);
        final PeerAddress domainStatus = new PeerAddress();
        domainStatus.setAddress(BytesValue.of(rpcStatus.getAddress().toByteArray()));
        domainStatus.setPort(rpcStatus.getPort());
        domainStatus.setPeerId(BytesValue.of(rpcStatus.getPeerID().toByteArray()));
        return domainStatus;
      };


  public ModelConverter<PeerAddress, types.Node.PeerAddress> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
