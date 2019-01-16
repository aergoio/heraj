/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.Peer;
import hera.exception.RpcException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import types.Node;
import types.P2P.NewBlockNotice;
import types.Rpc;

public class PeerConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainStatusConverter =
      new BlockchainStatusConverterFactory().create();

  protected final Function1<Peer, Rpc.Peer> domainConverter = new Function1<Peer, Rpc.Peer>() {
    public Rpc.Peer apply(final Peer domainPeer) {
      logger.trace("Domain peer: {}", domainPeer);
      try {
        return Rpc.Peer.newBuilder()
            .setAddress(Node.PeerAddress.newBuilder()
                .setAddress(domainPeer.getAddress().getHostAddress())
                .setPort(domainPeer.getPort())
                .setPeerID(ByteString.copyFrom(Base58Utils.decode(domainPeer.getPeerId())))
                .build())
            .setBestblock(NewBlockNotice.newBuilder()
                .setBlockHash(
                    copyFrom(domainPeer.getBlockchainStatus().getBestBlockHash().getBytesValue()))
                .setBlockNo(domainPeer.getBlockchainStatus().getBestHeight()).build())
            .setState(domainPeer.getState())
            .setHidden(domainPeer.isHidden())
            .build();
      } catch (IOException e) {
        throw new RpcException("PeerId decoding failed", e);
      }
    }
  };

  protected final Function1<Rpc.Peer, Peer> rpcConverter = new Function1<Rpc.Peer, Peer>() {

    @Override
    public Peer apply(final Rpc.Peer rpcPeer) {
      logger.trace("Rpc peer: {}", rpcPeer);
      try {
        final BlockchainStatus blockchainStatus = new BlockchainStatus(
            rpcPeer.getBestblock().getBlockNo(),
            new BlockHash(BytesValue.of(rpcPeer.getBestblock().getBlockHash().toByteArray())));
        return new Peer(Inet6Address.getByName(rpcPeer.getAddress().getAddress()),
            rpcPeer.getAddress().getPort(),
            Base58Utils.encode(rpcPeer.getAddress().getPeerID().toByteArray()),
            blockchainStatus,
            rpcPeer.getState(),
            rpcPeer.getHidden());
      } catch (UnknownHostException e) {
        throw new RpcException("Invalid peer host name", e);
      }
    }
  };

  public ModelConverter<Peer, Rpc.Peer> create() {
    return new ModelConverter<Peer, Rpc.Peer>(domainConverter, rpcConverter);
  }

}
