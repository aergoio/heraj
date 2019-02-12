/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function0;
import hera.api.function.Function1;
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
import hera.api.model.PeerId;
import hera.api.model.RawTransaction;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.exception.InvalidAuthentiationException;
import hera.exception.UnbindedAccountException;
import hera.exception.UnbindedKeyStoreException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import hera.keystore.KeyStore;
import java.util.List;
import org.slf4j.Logger;

public abstract class AbstractWallet extends LookupWallet implements Wallet {

  protected final Logger logger = getLogger(getClass());

  protected KeyStore keyStore;

  protected Account account;

  protected TransactionTrier trier;

  protected AbstractWallet(final AergoClient aergoClient,
      final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient);

    logger.debug("Binded nonce refresh: {}", tryCountAndInterval);
    final TransactionTrier trier = new TransactionTrier(tryCountAndInterval);
    trier.setAccountProvider(new Function0<Account>() {
      @Override
      public Account apply() {
        return getAccount();
      }
    });
    trier.setNonceSynchronizer(new Runnable() {
      @Override
      public void run() {
        getAccount().bindState(getAccountState());
      }
    });
    this.trier = trier;
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
  public List<AccountAddress> listKeyStoreAddresses() {
    return getKeyStore().listStoredAddresses();
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      this.account = getKeyStore().unlock(authentication);
      getCurrentAccount().bindState(getAccountState());
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
  public Account getAccount() {
    if (null == this.account) {
      throw new UnbindedAccountException();
    }
    return this.account;
  }

  @Override
  public Account getCurrentAccount() {
    return getAccount();
  }

  @Override
  public AccountState getAccountState() {
    return getAccountState(getAccount());
  }

  @Override
  public AccountState getCurrentAccountState() {
    return getAccountState();
  }

  @Override
  public StakingInfo getStakingInfo() {
    return getStakingInfo(getAccount());
  }

  @Override
  public List<VotingInfo> listVotes() {
    return listVotesOf(getAccount());
  }

  @Override
  public long getRecentlyUsedNonce() {
    return getAccount().getRecentlyUsedNonce();
  }

  @Override
  public long incrementAndGetNonce() {
    return getAccount().incrementAndGetNonce();
  }

  @Override
  public TxHash createName(final String name) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getAccountOperation()
            .createName(getAccount(), name, nonce);
      }
    });
  }

  @Override
  public TxHash updateName(final String name, final AccountAddress newOwner) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getAccountOperation()
            .updateName(getAccount(), name, newOwner, nonce);
      }
    });
  }

  @Override
  public TxHash stake(final Aer amount) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getAccountOperation()
            .stake(getAccount(), amount, nonce);
      }
    });
  }

  @Override
  public TxHash unstake(final Aer amount) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getAccountOperation()
            .unstake(getAccount(), amount, nonce);
      }
    });
  }

  @Override
  public TxHash vote(final PeerId peerId) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getBlockchainOperation()
            .vote(getAccount(), peerId, nonce);
      }
    });
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return getAergoClient().getAccountOperation().sign(getAccount(), rawTransaction);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return getAergoClient().getAccountOperation().verify(getAccount(), transaction);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        final RawTransaction rawTransaction = RawTransaction.newBuilder()
            .from(getAccount())
            .to(recipient)
            .amount(amount)
            .nonce(nonce)
            .fee(fee)
            .payload(payload)
            .build();
        return getAergoClient().getTransactionOperation().commit(sign(rawTransaction));
      }
    });
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        final RawTransaction rawTransaction = RawTransaction.newBuilder()
            .from(getAccount())
            .to(recipient)
            .amount(amount)
            .nonce(nonce)
            .fee(fee)
            .payload(payload)
            .build();
        return getAergoClient().getTransactionOperation().commit(sign(rawTransaction));
      }
    });
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    return commit(sign(rawTransaction));
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    return trier.request(new Function0<TxHash>() {

      @Override
      public TxHash apply() {
        return getAergoClient().getTransactionOperation().commit(signedTransaction);
      }
    }, new Function1<Long, TxHash>() {

      @Override
      public TxHash apply(final Long nonce) {
        final RawTransaction withNewNonce =
            RawTransaction.copyOf(signedTransaction, nonce.longValue());
        return getAergoClient().getTransactionOperation().commit(sign(withNewNonce));
      }
    });
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getContractOperation().deploy(getAccount(),
            contractDefinition, nonce, fee);
      }
    }).adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    return (ContractTxHash) trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        return getAergoClient().getContractOperation().execute(getAccount(),
            contractInvocation, nonce, fee);
      }
    }).adapt(ContractTxHash.class);
  }

}
