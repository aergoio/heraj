/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

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

public class TransactionTrier {

  protected final Logger logger = getLogger(getClass());

  protected final TryCountAndInterval tryCountAndInterval;

  @Getter
  @Setter
  protected Function0<Account> accountProvider;

  @Getter
  @Setter
  protected Runnable nonceSynchronizer;

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
    boolean success = false;
    if (null != firstTrier) {
      try {
        txHash = firstTrier.apply();
        success = null != txHash;
      } catch (WalletException e) {
        logger.debug("First try failure by {}", e.toString());
      }
    }

    int i = tryCountAndInterval.getCount();
    while (0 <= i && !success) {
      try {
        txHash = transactionRequester.apply(accountProvider.apply().incrementAndGetNonce());
        success = true;
      } catch (WalletException e) {
        if (!isNonceRelatedException(e)) {
          throw e;
        }
        logger.info("Request failed with invalid nonce.. try left: {}", i);
        nonceSynchronizer.run();
        tryCountAndInterval.trySleep();
        --i;
      }
    }
    return txHash;
  }

  protected boolean isNonceRelatedException(final WalletException e) {
    if (!(e.getCause() instanceof RpcCommitException)) {
      return false;
    }
    final RpcCommitException cause = (RpcCommitException) e.getCause();
    return cause.getCommitStatus() == RpcCommitException.CommitStatus.NONCE_TOO_LOW
        || cause.getCommitStatus() == RpcCommitException.CommitStatus.TX_HAS_SAME_NONCE;
  }

}

