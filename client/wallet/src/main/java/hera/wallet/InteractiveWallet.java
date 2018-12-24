/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.TryCountAndInterval;
import hera.api.tupleorerror.Function1;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.exception.CommitException.CommitStatus;
import hera.exception.InvalidAuthentiationException;
import hera.exception.UnbindedAccountException;
import hera.exception.UnbindedKeyStoreException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class InteractiveWallet extends LookupWallet implements Wallet {

  protected final Logger logger = getLogger(getClass());

  protected KeyStore keyStore;

  protected Account account;

  @Getter(value = AccessLevel.PROTECTED)
  protected final TryCountAndInterval nonceRefreshTryCountAndInterval;

  protected InteractiveWallet(final AergoClient aergoClient,
      final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient);
    logger.debug("Binded nonce refresh: {}", tryCountAndInterval);
    this.nonceRefreshTryCountAndInterval = tryCountAndInterval;
  }

  protected KeyStore getKeyStore() {
    if (null == this.keyStore) {
      throw new UnbindedKeyStoreException();
    }
    return this.keyStore;
  }

  @Override
  public void saveKey(final AergoKey key, final String password) {
    getKeyStore().saveKey(key, password);
  }

  @Override
  public String exportKey(final Authentication authentication) {
    return getKeyStore().export(authentication).getEncoded();
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      this.account = getKeyStore().unlock(authentication);
      getCurrentAccount().bindState(getCurrentAccountState());
      return true;
    } catch (InvalidAuthentiationException e) {
      return false;
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      getKeyStore().lock(authentication);
      return true;
    } catch (InvalidAuthentiationException e) {
      return false;
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public boolean storeKeyStore(final String path, final String password) {
    try {
      getKeyStore().store(path, password);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Account getCurrentAccount() {
    if (null == this.account) {
      throw new UnbindedAccountException();
    }
    return this.account;
  }

  @Override
  public AccountState getCurrentAccountState() {
    return getAergoClient().getAccountOperation().getState(getCurrentAccount());
  }

  @Override
  public long getRecentlyUsedNonce() {
    return getCurrentAccount().getNonce();
  }

  @Override
  public long incrementAndGetNonce() {
    return getCurrentAccount().incrementAndGetNonce();
  }

  @Override
  public TxHash createName(final String name) {
    return sendRequestWithNonce(nonce -> getAergoClient().getAccountOperation()
        .createName(getCurrentAccount(), name, nonce));
  }

  @Override
  public TxHash updateName(final String name, final AccountAddress newOwner) {
    return sendRequestWithNonce(nonce -> getAergoClient().getAccountOperation()
        .updateName(getCurrentAccount(), name, newOwner, nonce));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return getAergoClient().getAccountOperation().sign(getCurrentAccount(), rawTransaction);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return getAergoClient().getAccountOperation().verify(getCurrentAccount(), transaction);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(getCurrentAccount())
        .to(recipient)
        .amount(amount)
        .nonce(getCurrentAccount().incrementAndGetNonce())
        .fee(fee)
        .payload(payload)
        .build();
    return commit(rawTransaction);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(getCurrentAccount())
        .to(recipient)
        .amount(amount)
        .nonce(getCurrentAccount().incrementAndGetNonce())
        .fee(fee)
        .payload(payload)
        .build();
    return commit(rawTransaction);
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    return commit(sign(rawTransaction));
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    TxHash txHash = null;
    Transaction commitTarget = signedTransaction;
    int i = getNonceRefreshTryCountAndInterval().getCount();
    while (0 <= i && null == txHash) {
      try {
        txHash = getAergoClient().getTransactionOperation().commit(commitTarget);
        // if success, cache an nonce
        getCurrentAccount().setNonce(commitTarget.getNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
          getNonceRefreshTryCountAndInterval().trySleep();
          commitTarget = sign(
              RawTransaction.copyOf(signedTransaction, getCurrentAccount().incrementAndGetNonce()));
          --i;
        } else {
          throw e;
        }
      }
    }
    return txHash;
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    return (ContractTxHash) sendRequestWithNonce(
        n -> getAergoClient().getContractOperation().deploy(getCurrentAccount(),
            contractDefinition, n, fee));
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    return (ContractTxHash) sendRequestWithNonce(
        n -> getAergoClient().getContractOperation().execute(getCurrentAccount(),
            contractInvocation, n, fee));
  }

  protected TxHash sendRequestWithNonce(final Function1<Long, TxHash> requester) {
    TxHash txHash = null;
    int i = getNonceRefreshTryCountAndInterval().getCount();
    while (0 <= i && null == txHash) {
      try {
        txHash = requester.apply(getCurrentAccount().incrementAndGetNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
          getNonceRefreshTryCountAndInterval().trySleep();
          --i;
        } else {
          throw e;
        }
      }
    }
    return txHash;
  }

  protected boolean isNonceRelatedException(final CommitException e) {
    return e.getCommitStatus() == CommitStatus.NONCE_TOO_LOW
        || e.getCommitStatus() == CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected void syncNonceWithServer() {
    getCurrentAccount().bindState(getCurrentAccountState());
  }

}
