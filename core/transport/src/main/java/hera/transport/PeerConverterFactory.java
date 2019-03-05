/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.exception.RpcException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import types.Rpc;

public class PeerConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainStatusConverter =
      new BlockchainStatusConverterFactory().create();

  protected final Function1<Peer, Rpc.Peer> domainConverter = new Function1<Peer, Rpc.Peer>() {
    public Rpc.Peer apply(final Peer domainPeer) {
      throw new UnsupportedOperationException();
    }
  };

  protected final Function1<Rpc.Peer, Peer> rpcConverter = new Function1<Rpc.Peer, Peer>() {

    @Override
    public Peer apply(final Rpc.Peer rpcPeer) {
      try {
        logger.trace("Rpc peer to convert: {}", rpcPeer);
        final BlockchainStatus blockchainStatus = new BlockchainStatus(
            rpcPeer.getBestblock().getBlockNo(),
            new BlockHash(of(rpcPeer.getBestblock().getBlockHash().toByteArray())));
        final Peer domainPeer = new Peer(Inet6Address.getByName(rpcPeer.getAddress().getAddress()),
            rpcPeer.getAddress().getPort(),
            new PeerId(of(rpcPeer.getAddress().getPeerID().toByteArray())),
            blockchainStatus,
            rpcPeer.getState(),
            rpcPeer.getHidden(),
            rpcPeer.getLashCheck(),
            rpcPeer.getSelfpeer());
        logger.trace("Domain peer converted: {}", domainPeer);
        return domainPeer;
      } catch (UnknownHostException e) {
        throw new RpcException("Invalid peer host name", e);
      }
    }
  };

  public ModelConverter<Peer, Rpc.Peer> create() {
    return new ModelConverter<Peer, Rpc.Peer>(domainConverter, rpcConverter);
  }

}
