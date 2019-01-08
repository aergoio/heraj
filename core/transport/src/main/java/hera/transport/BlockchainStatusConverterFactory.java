/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import org.slf4j.Logger;
import types.Rpc;

public class BlockchainStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<BlockchainStatus, Rpc.BlockchainStatus> domainConverter =
      new Function1<BlockchainStatus, Rpc.BlockchainStatus>() {

        @Override
        public Rpc.BlockchainStatus apply(final BlockchainStatus domainBlockchainStatus) {
          logger.trace("Domain blockchain status: {}", domainBlockchainStatus);
          return Rpc.BlockchainStatus.newBuilder()
              .setBestBlockHash(copyFrom(domainBlockchainStatus.getBestBlockHash().getBytesValue()))
              .setBestHeight(domainBlockchainStatus.getBestHeight()).build();
        }
      };

  protected final Function1<Rpc.BlockchainStatus, BlockchainStatus> rpcConverter =
      new Function1<Rpc.BlockchainStatus, BlockchainStatus>() {

        @Override
        public BlockchainStatus apply(final Rpc.BlockchainStatus rpcBlockchainStatus) {
          logger.trace("Rpc blockchain status: {}", rpcBlockchainStatus);
          return new BlockchainStatus(
              rpcBlockchainStatus.getBestHeight(),
              new BlockHash(BytesValue.of(rpcBlockchainStatus.getBestBlockHash().toByteArray())));
        }
      };

  public ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> create() {
    return new ModelConverter<BlockchainStatus, Rpc.BlockchainStatus>(domainConverter,
        rpcConverter);
  }

}
