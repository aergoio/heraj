package hera.wallet.internal;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function0;
import hera.api.function.Function2;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.ChainIdHash;
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.client.AergoClient;
import hera.exception.RpcCommitException;
import hera.exception.RpcException;
import hera.key.Signer;
import hera.model.TryCountAndInterval;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class Trier {

  protected final Logger logger = getLogger(getClass());

  @Getter
  @Setter
  protected TryCountAndInterval tryCountAndInterval;

  @Getter
  @Setter
  protected NonceProvider nonceProvider;

  @Getter
  @Setter
  protected Function0<AergoClient> clientProvider;

  /**
   * Try transaction request with a requester. If {@code transactionRequester} fails, retry after
   * refreshing nonce.
   *
   * @param signer a signer to sign transaction
   * @param transactionRequester a transaction requester
   * @return a transaction hash
   */
  public TxHash request(final Signer signer,
      final Function2<Signer, Long, TxHash> transactionRequester) {
    assertNotNull(signer);
    assertNotNull(transactionRequester);
    logger.debug("Transaction try with signer: {}, transactionRequester: {}", signer,
        transactionRequester);

    TxHash txHash = null;
    RpcException recentException = null;

    int i = tryCountAndInterval.getCount();
    while (0 <= i && null == txHash) {
      final long nonceToBeUsed = nonceProvider.incrementAndGetNonce(signer.getPrincipal());
      try {
        txHash = transactionRequester.apply(signer, nonceToBeUsed);
      } catch (RpcException e) {
        recentException = e;
        if (isNonceRelatedException(e)) {
          logger.info("Request failed with invalid nonce.. try left: {}", i);
          syncNonceOf(signer.getPrincipal());
        } else if (isChainIdHashException(e)) {
          logger.info("Request failed with invalid chain id hash.. try left: {}", i);
          nonceProvider.bindNonce(signer.getPrincipal(), nonceToBeUsed - 1);
          syncChainIdHash(clientProvider.apply());
        } else {
          throw e;
        }
        tryCountAndInterval.trySleep();
        --i;
      }
    }

    if (null == txHash && null != recentException) {
      throw recentException;
    }

    return txHash;
  }

  protected boolean isNonceRelatedException(final RpcException e) {
    if (!(e instanceof RpcCommitException)) {
      return false;
    }
    final RpcCommitException cause = (RpcCommitException) e;
    return cause.getCommitStatus() == RpcCommitException.CommitStatus.NONCE_TOO_LOW
        || cause.getCommitStatus() == RpcCommitException.CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected boolean isChainIdHashException(final RpcException e) {
    if (!(e instanceof RpcCommitException)) {
      return false;
    }
    final RpcCommitException cause = (RpcCommitException) e;
    // FIXME : no other way?
    return cause.getMessage().indexOf("invalid chain id") != -1;
  }

  protected void syncNonceOf(final AccountAddress address) {
    final AergoClient client = clientProvider.apply();
    final AccountState recentState = client.getAccountOperation().getState(address);
    nonceProvider.bindNonce(recentState);
  }

  protected void syncChainIdHash(final AergoClient client) {
    // TODO: aergo client should have a role handling chain id hash
    final ChainIdHash chainIdHash = client.getBlockchainOperation().getChainIdHash();
    client.cacheChainIdHash(chainIdHash);
  }

}
