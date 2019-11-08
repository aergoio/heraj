/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.TxHash;
import hera.api.model.internal.TryCountAndInterval;
import hera.exception.RpcCommitException;
import hera.exception.WalletException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

@Deprecated
public class TransactionTrier {

  protected final Logger logger = getLogger(getClass());

  protected final TryCountAndInterval tryCountAndInterval;

  @Getter
  @Setter
  protected Function0<Account> accountProvider;

  @Getter
  @Setter
  protected Runnable nonceSynchronizer;

  @Getter
  @Setter
  protected Runnable chainIdSynchronizer;

  public TransactionTrier(final TryCountAndInterval tryCountAndInterval) {
    logger.debug("Binded nonce refresh: {}", tryCountAndInterval);
    this.tryCountAndInterval = tryCountAndInterval;
  }

  /**
   * Try transaction request with a requester. If {@code transactionRequester} fails, retry after
   * refreshing nonce.
   *
   * @param transactionRequester a transaction requester
   * @return a transaction hash
   */
  public TxHash request(Function1<Long, TxHash> transactionRequester) {
    return request(null, transactionRequester);
  }

  /**
   * Try transaction request with a requester along with first trier. {@code transactionRequester}
   * is invoked when {@code firstTrier} has failed. If {@code transactionRequester} fails, retry
   * after refreshing nonce.
   *
   * @param firstTrier a first trier
   * @param transactionRequester a transaction requester
   * @return a transaction hash
   */
  public TxHash request(Function0<TxHash> firstTrier,
      Function1<Long, TxHash> transactionRequester) {
    assertNotNull(transactionRequester);
    logger.debug("Transaction try with firstTrier: {}, transactionRequester: {}", firstTrier,
        transactionRequester);

    TxHash txHash = null;
    Exception exception = null;
    boolean success = false;
    if (null != firstTrier) {
      try {
        txHash = firstTrier.apply();
        success = null != txHash;
      } catch (Exception e) {
        logger.debug("First try failure by {}", e.toString());
        exception = e;
      }
    }

    int i = tryCountAndInterval.getCount();
    while (0 <= i && !success) {
      try {
        txHash = transactionRequester.apply(accountProvider.apply().incrementAndGetNonce());
        success = true;
      } catch (Exception e) {
        if (isNonceRelatedException(e)) {
          logger.info("Request failed with invalid nonce.. try left: {}", i);
          nonceSynchronizer.run();
        } else if (isChainIdHashException(e)) {
          logger.info("Request failed with invalid chain id hash.. try left: {}", i);
          chainIdSynchronizer.run();
          nonceSynchronizer.run();
        } else {
          throw e;
        }
        exception = e;
        tryCountAndInterval.trySleep();
        --i;
      }
    }

    if (!success) {
      throw new WalletException(exception);
    }

    return txHash;
  }

  protected boolean isNonceRelatedException(final Exception e) {
    if (!(e instanceof RpcCommitException)) {
      return false;
    }
    final RpcCommitException cause = (RpcCommitException) e;
    return cause.getCommitStatus() == RpcCommitException.CommitStatus.NONCE_TOO_LOW
        || cause.getCommitStatus() == RpcCommitException.CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected boolean isChainIdHashException(final Exception e) {
    if (!(e instanceof RpcCommitException)) {
      return false;
    }
    final RpcCommitException cause = (RpcCommitException) e;
    // FIXME : no another way?
    return cause.getMessage().indexOf("invalid chain id") != -1;
  }

}

