package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.exception.CommitException;
import hera.exception.HerajException;
import hera.key.Signer;
import hera.util.ThreadUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
class NonceRefreshingTxRequester implements TxRequester {

  protected final transient Logger logger = getLogger(getClass());

  @NonNull
  protected final ClientProvider clientProvider;

  @NonNull
  protected final TryCountAndInterval tryCountAndInterval;

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();

  @Override
  public TxHash request(final Signer signer, final TxRequestFunction requester) {
    assertNotNull(signer, "Signer must not null");
    assertNotNull(requester, "Requester must not null");
    logger.debug("Transaction try with signer: {}, requester: {}", signer,
        requester);

    TxHash txHash = null;
    HerajException error = null;

    final long sleepInterval = tryCountAndInterval.getInterval().toMilliseconds();
    int count = tryCountAndInterval.getCount();
    while (0 <= count && null == txHash) {
      final long nonce = nonceProvider.incrementAndGetNonce(signer.getPrincipal());
      try {
        txHash = requester.apply(signer, nonce);
      } catch (HerajException e) {
        error = e;
        if (isNonceRelatedException(e)) {
          final CommitException commitException = (CommitException) e;
          logger.debug("Request failed with {}. Refresh it (try left: {})",
              commitException.getCommitStatus(), count);
          syncNonce(signer.getPrincipal());
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

  protected boolean isNonceRelatedException(final HerajException e) {
    if (!(e instanceof CommitException)) {
      return false;
    }

    final CommitException cause = (CommitException) e;
    return cause.getCommitStatus() == CommitException.CommitStatus.NONCE_TOO_LOW
        || cause.getCommitStatus() == CommitException.CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected void syncNonce(final AccountAddress address) {
    final AccountState state = clientProvider.getClient().getAccountOperation().getState(address);
    logger.debug("Fetched nonce for {} is {}", address, state.getNonce());
    nonceProvider.bindNonce(state);
  }

}
