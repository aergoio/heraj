/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.Invocation;
import hera.RequestMethod;
import hera.Response;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import lombok.Getter;
import org.slf4j.Logger;

class InvalidChainIdHashHandler extends ComparableFailoverHandler {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final int priority = 1;

  protected final BlockchainMethods blockchainMethods = new BlockchainMethods();

  InvalidChainIdHashHandler() {

  }

  @Override
  public <T> void handle(final Invocation<T> invocation, final Response<T> response) {
    try {
      final Context current = ContextHolder.current();

      final ChainIdHashHolder chainIdHashHolder = current.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
      if (null == chainIdHashHolder) {
        throw new UnsupportedOperationException("No chain id hash holder");
      }

      final RequestMethod<BlockchainStatus> requestMethod = blockchainMethods.getBlockchainStatus();
      final ChainIdHash chainIdHash = requestMethod.invoke(emptyList()).getChainIdHash();
      chainIdHashHolder.put(chainIdHash);

      final T ret = invocation.invoke();
      response.success(ret);
    } catch (Exception e) {
      response.fail(e);
    }
  }

}
