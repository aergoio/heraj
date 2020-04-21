/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.Invocation;
import hera.RequestMethod;
import hera.Response;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.exception.CommitException;
import hera.exception.HerajException;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
class InvalidChainIdHashHandler extends ComparableFailoverHandler {

  @Getter
  protected final int priority = 1;

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  // not final for mock
  @ToString.Exclude
  protected BlockchainMethods blockchainMethods = new BlockchainMethods();

  InvalidChainIdHashHandler() {

  }

  @Override
  public <T> Response<T> handle(final Invocation<T> invocation, final Response<T> response) {
    try {
      logger.debug("Handle {} with {}", response.getError(), this);

      if (null == response.getError() || !(response.getError() instanceof CommitException)) {
        return response;
      }

      // FIXME: no other way to handle it?
      final CommitException commitException = (CommitException) response.getError();
      if (!commitException.getMessage().contains("invalid chain id hash")) {
        return response;
      }

      final Context current = ContextHolder.current();
      final ChainIdHashHolder chainIdHashHolder = current.get(GRPC_VALUE_CHAIN_ID_HASH_HOLDER);
      if (null == chainIdHashHolder) {
        throw new HerajException("No chain id hash holder");
      }

      final RequestMethod<BlockchainStatus> requestMethod = blockchainMethods.getBlockchainStatus();
      final ChainIdHash chainIdHash = requestMethod.invoke().getChainIdHash();
      logger.debug("Fetched ChainIdHash: {}", chainIdHash);
      chainIdHashHolder.put(chainIdHash);
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException("Unexpected error", e);
    }

    Response<T> next;
    try {
      final T ret = invocation.invoke();
      next = Response.success(ret);
    } catch (Exception e) {
      next = Response.fail(e);
    }
    return next;
  }

}
