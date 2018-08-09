/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BlockchainStatus;
import hera.api.model.Hash;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;

public class BlockchainConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<BlockchainStatus, Rpc.BlockchainStatus> domainConverter =
      domainBlockchainStatus -> {
        logger.trace("Domain status: {}", domainBlockchainStatus);
        return Rpc.BlockchainStatus.newBuilder()
            .setBestBlockHash(copyFrom(domainBlockchainStatus.getBestBlockHash()))
            .setBestHeight(domainBlockchainStatus.getBestHeight())
            .build();
      };

  protected final Function<Rpc.BlockchainStatus, BlockchainStatus> rpcConverter =
      rpcBlockchainStatus -> {
        logger.trace("Blockchain status: {}", rpcBlockchainStatus);
        final BlockchainStatus domainBlockchainStatus = new BlockchainStatus();
        domainBlockchainStatus.setBestHeight(rpcBlockchainStatus.getBestHeight());
        domainBlockchainStatus.setBestBlockHash(
            new Hash(rpcBlockchainStatus.getBestBlockHash().toByteArray()));
        return domainBlockchainStatus;
      };

  public ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
