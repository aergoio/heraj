package hera.client;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.TryCountAndInterval;
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.exception.CommitException;
import hera.key.Signer;
import hera.util.ThreadUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class NonceRefreshingTxRequester implements TxRequester {

  protected final transient Logger logger = getLogger(getClass());

  @NonNull
  protected final TryCountAndInterval tryCountAndInterval;

  @NonNull
  protected final NonceProvider nonceProvider;

  @Override
  public TxHash request(final AergoClient aergoClient, final Signer signer,
      final TxRequestFunction requestFunction) throws Exception {
    assertNotNull(aergoClient, "AergoClient must not null");
    assertNotNull(signer, "Signer must not null");
    assertNotNull(requestFunction, "RequestFunction must not null");
    logger.debug("Transaction try with signer: {}, requestFunction: {}", signer,
        requestFunction);

    TxHash txHash = null;
    Exception error = null;

    final long sleepInterval = tryCountAndInterval.getInterval().toMilliseconds();
    int count = tryCountAndInterval.getCount();
    while (0 <= count && null == txHash) {
      final long nonce = nonceProvider.incrementAndGetNonce(signer.getPrincipal());
      try {
        txHash = requestFunction.apply(signer, nonce);
      } catch (Exception e) {
        error = e;
        if (isNonceRelatedException(e)) {
          final CommitException commitException = (CommitException) e;
          logger.debug("Request failed with {}. Refresh it (try left: {})",
              commitException.getCommitStatus(), count);
          syncNonce(aergoClient, signer.getPrincipal());
        } else {
          throw e;
        }

        ThreadUtils.trySleep(sleepInterval);
        --count;
      }
    }

    if (null == txHash && null != error) {
      throw error;
    }

    return txHash;
  }

  protected boolean isNonceRelatedException(final Exception e) {
    if (!(e instanceof CommitException)) {
      return false;
    }

    final CommitException cause = (CommitException) e;
    return cause.getCommitStatus() == CommitException.CommitStatus.NONCE_TOO_LOW
        || cause.getCommitStatus() == CommitException.CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected void syncNonce(final AergoClient aergoClient, final AccountAddress address) {
    final AccountState state = aergoClient.getAccountOperation().getState(address);
    logger.debug("Fetched nonce for {} is {}", address, state.getNonce());
    nonceProvider.bindNonce(state);
  }

}
