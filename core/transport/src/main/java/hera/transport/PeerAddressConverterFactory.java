/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static com.google.protobuf.ByteString.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BytesValue;
import hera.api.model.PeerAddress;
import java.util.function.Function;
import org.slf4j.Logger;

public class PeerAddressConverterFactory {

  protected final Logger logger = getLogger(getClass());

  protected final Function<PeerAddress, types.Node.PeerAddress> domainConverter =
      domainPeerAddress -> {
        logger.trace("Domain status: {}", domainPeerAddress);
        return types.Node.PeerAddress.newBuilder()
            .setAddress(copyFrom(domainPeerAddress.getAddress().getValue()))
            .setPort(domainPeerAddress.getPort())
            .setPeerID(copyFrom(domainPeerAddress.getPeerId()))
            .build();
      };

  protected final Function<types.Node.PeerAddress, PeerAddress> rpcConverter =
      rpcStatus -> {
        logger.trace("Blockchain status: {}", rpcStatus);
        final PeerAddress domainStatus = new PeerAddress();
        domainStatus.setAddress(BytesValue.of(rpcStatus.getAddress().toByteArray()));
        domainStatus.setPort(rpcStatus.getPort());
        domainStatus.setPeerId(rpcStatus.getPeerID().toByteArray());
        return domainStatus;
      };


  public ModelConverter<PeerAddress, types.Node.PeerAddress> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
