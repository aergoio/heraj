/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.Peer;
import hera.exception.RpcException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Node;
import types.P2P.NewBlockNotice;
import types.Rpc;

public class PeerConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainStatusConverter =
      new BlockchainStatusConverterFactory().create();

  protected final Function<Peer, Rpc.Peer> domainConverter = domainPeer -> {
    logger.trace("Domain peer: {}", domainPeer);
    try {
      return Rpc.Peer.newBuilder()
          .setAddress(Node.PeerAddress.newBuilder()
              .setAddress(ByteString.copyFrom(domainPeer.getAddress().getAddress()))
              .setPort(domainPeer.getPort())
              .setPeerID(ByteString.copyFrom(Base58Utils.decode(domainPeer.getPeerId()))).build())
          .setBestblock(NewBlockNotice.newBuilder()
              .setBlockHash(
                  copyFrom(domainPeer.getBlockchainStatus().getBestBlockHash().getBytesValue()))
              .setBlockNo(domainPeer.getBlockchainStatus().getBestHeight()).build())
          .setState(domainPeer.getState()).build();
    } catch (IOException e) {
      throw new RpcException("PeerId decoding failed", e);
    }
  };

  protected final Function<Rpc.Peer, Peer> rpcConverter = rpcPeer -> {
    logger.trace("Rpc peer: {}", rpcPeer);
    try {
      final Peer domainStatus = new Peer();
      domainStatus
          .setAddress(Inet6Address.getByAddress(rpcPeer.getAddress().getAddress().toByteArray()));
      domainStatus.setPort(rpcPeer.getAddress().getPort());
      domainStatus.setPeerId(Base58Utils.encode(rpcPeer.getAddress().getPeerID().toByteArray()));

      final BlockchainStatus blockchainStatus = new BlockchainStatus();
      blockchainStatus.setBestBlockHash(
          new BlockHash(BytesValue.of(rpcPeer.getBestblock().getBlockHash().toByteArray())));
      blockchainStatus.setBestHeight(rpcPeer.getBestblock().getBlockNo());
      domainStatus.setBlockchainStatus(blockchainStatus);

      domainStatus.setState(rpcPeer.getState());
      return domainStatus;
    } catch (UnknownHostException e) {
      throw new RpcException("Invalid peer host name", e);
    }
  };


  public ModelConverter<Peer, Rpc.Peer> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
