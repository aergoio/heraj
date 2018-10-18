/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.Peer;
import hera.exception.RpcException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.function.Function;
import org.slf4j.Logger;

public class PeerConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Peer, types.Node.PeerAddress> domainConverter = domainPeerAddress -> {
    logger.trace("Domain peer address: {}", domainPeerAddress);
    try {
      return types.Node.PeerAddress.newBuilder()
          .setAddress(ByteString.copyFrom(domainPeerAddress.getAddress().getAddress()))
          .setPort(domainPeerAddress.getPort())
          .setPeerID(ByteString.copyFrom(Base58Utils.decode(domainPeerAddress.getPeerId())))
          .build();
    } catch (IOException e) {
      throw new RpcException("PeerId decoding failed", e);
    }
  };

  protected final Function<types.Node.PeerAddress, Peer> rpcConverter = rpcStatus -> {
    logger.trace("Rpc peer address: {}", rpcStatus);
    try {
      final Peer domainStatus = new Peer();
      domainStatus.setAddress(Inet6Address.getByAddress(rpcStatus.getAddress().toByteArray()));
      domainStatus.setPort(rpcStatus.getPort());
      domainStatus.setPeerId(Base58Utils.encode(rpcStatus.getPeerID().toByteArray()));
      return domainStatus;
    } catch (UnknownHostException e) {
      throw new RpcException("Invalid peer host name", e);
    }
  };


  public ModelConverter<Peer, types.Node.PeerAddress> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
