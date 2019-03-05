/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

public class ExceptionHandler {

  protected final Logger logger = getLogger(getClass());

  /**
   * Handle exception.
   *
   * @param e an exception to handle
   * @return handled exception
   */
  public WalletException handle(final Exception e) {
    logger.debug("Handle exception {}", e.toString());
    if (e instanceof WalletException) {
      return (WalletException) e;
    } else if (e instanceof RpcCommitException) {
      return new WalletCommitException(e);
    } else if (e instanceof RpcConnectionException) {
      return new WalletConnectionException(e);
    } else if (e instanceof RpcException) {
      return new WalletRpcException(e);
    } else {
      return new WalletException("Unexpected one", e);
    }
  }

}
