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
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.exception.UnbindedAccountException;
import hera.exception.UnbindedKeyStoreException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import hera.key.AergoSignVerifier;
import hera.keystore.KeyStore;
import java.util.List;
import org.slf4j.Logger;

public abstract class AbstractWallet extends QueryWallet implements Wallet {

  protected final transient Logger logger = getLogger(getClass());

  protected KeyStore keyStore;

  protected Account account;

  protected TransactionTrier trier;

  protected AbstractWallet(final AergoClient aergoClient,
      final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient);
    logger.debug("Binded nonce refresh: {}", tryCountAndInterval);
    this.trier = buildTrier(tryCountAndInterval);
  }

  protected TransactionTrier buildTrier(final TryCountAndInterval tryCountAndInterval) {
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
    trier.setChainIdSynchronizer(new Runnable() {
      @Override
      public void run() {
        cacheChainIdHash(aergoClient.getBlockchainOperation().getChainIdHash());
      }
    });

    return trier;
  }

  protected KeyStore getKeyStore() {
    if (null == this.keyStore) {
      throw new UnbindedKeyStoreException();
    }
    return this.keyStore;
  }

  @Override
  public void saveKey(final AergoKey key, final String password) {
    try {
      saveKey(key, key.getAddress(), password);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public void saveKey(final AergoKey key, final Identity identity,
      final String password) {
    try {
      logger.debug("Save key {} with identity {}", key, identity);
      getKeyStore().saveKey(key, Authentication.of(identity, password));
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public String exportKey(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      return getKeyStore().export(authentication).getEncoded();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Identity> listKeyStoreIdentities() {
    try {
      logger.debug("List key store identities");
      return getKeyStore().listIdentities();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock account with authentication: {}", authentication);
      final Account unlocked = getKeyStore().unlock(authentication);
      if (null == unlocked) {
        return false;
      }
      try {
        unlocked.bindState(getAccountState(unlocked));
      } catch (WalletException e) {
        logger.debug("Sync unlocked account state failed, address: {}", unlocked.getAddress());
      }
      this.account = unlocked;
      return true;
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock account with authentication: {}", authentication);
      return getKeyStore().lock(authentication);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public void storeKeyStore(final String path, final String password) {
    try {
      logger.debug("Store keystore to {}", path);
      getKeyStore().store(path, password);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
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
  public AccountState getAccountState() {
    return getAccountState(getAccount());
  }

  @Override
  public StakeInfo getStakingInfo() {
    return getStakingInfo(getAccount());
  }

  @Override
  public AccountTotalVote getVotes() {
    return getVotesOf(getAccount());
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
  public ChainIdHash getCachedChainIdHash() {
    return aergoClient.getCachedChainIdHash();
  }

  @Override
  public void cacheChainIdHash() {
    cacheChainIdHash(getChainIdHash());
  }

  @Override
  public void cacheChainIdHash(final ChainIdHash chainIdHash) {
    logger.info("Cache chain id: {}", chainIdHash);
    aergoClient.cacheChainIdHash(chainIdHash);
  }

  @Override
  public Account loadAccount(final Authentication authentication) {
    final boolean unlockResult = unlock(authentication);
    return unlockResult == true ? this.account : null;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign raw transaction {}", rawTransaction);
      return getAergoClient().getAccountOperation().sign(getAccount(), rawTransaction);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean verify(final Transaction transaction) {
    try {
      logger.debug("Verify signed transaction {}", transaction);
      final AergoSignVerifier verifier = new AergoSignVerifier();
      return verifier.verify(transaction);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash createName(final String name) {
    logger.debug("Create name {} to currently binded account", name);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getAccountOperation()
              .createName(getAccount(), name, nonce);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash updateName(final String name, final AccountAddress newOwner) {
    logger.debug("Change owner of name {} to {}", name, newOwner);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getAccountOperation()
              .updateName(getAccount(), name, newOwner, nonce);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash stake(final Aer amount) {
    logger.debug("Stake {}", amount);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getAccountOperation()
              .stake(getAccount(), amount, nonce);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash unstake(final Aer amount) {
    logger.debug("Unstake {}", amount);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getAccountOperation()
              .unstake(getAccount(), amount, nonce);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash voteBp(final List<String> candidates) {
    return vote("voteBP", candidates);
  }

  @Override
  public TxHash vote(final String voteId, final List<String> candidates) {
    logger.debug("Vote to {} with {}", voteId, candidates);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getBlockchainOperation()
              .vote(getAccount(), voteId, candidates, nonce);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash send(final String recipient, final Aer amount) {
    return send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final BytesValue payload) {
    return send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    logger.debug("Send request with recipient: {}, amount: {}, fee: {}, payload: {}", recipient,
        amount, fee, payload);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          final RawTransaction rawTransaction =
              RawTransaction.newBuilder(getCachedChainIdHash())
                  .from(getAccount())
                  .to(recipient)
                  .amount(amount)
                  .nonce(nonce)
                  .fee(fee)
                  .payload(payload)
                  .build();
          return getAergoClient().getTransactionOperation().commit(sign(rawTransaction));
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount) {
    return send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final BytesValue payload) {
    return send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    logger.debug("Send request with recipient: {}, amount: {}, fee: {}, payload: {}", recipient,
        amount, fee, payload);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          final RawTransaction rawTransaction =
              RawTransaction.newBuilder(getCachedChainIdHash())
                  .from(getAccount())
                  .to(recipient)
                  .amount(amount)
                  .nonce(nonce)
                  .fee(fee)
                  .payload(payload)
                  .build();
          return getAergoClient().getTransactionOperation().commit(sign(rawTransaction));
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    return commit(sign(rawTransaction));
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    logger.debug("Commit signed transaction: {}", signedTransaction);
    return trier.request(new Function0<TxHash>() {

      @Override
      public TxHash apply() {
        try {
          return getAergoClient().getTransactionOperation().commit(signedTransaction);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    }, new Function1<Long, TxHash>() {

      @Override
      public TxHash apply(final Long nonce) {
        try {
          final RawTransaction withNewNonce =
              signedTransaction.getRawTransaction().withNonce(nonce);
          return getAergoClient().getTransactionOperation().commit(sign(withNewNonce));
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    });
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition) {
    return deploy(contractDefinition, Fee.ZERO);
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    logger.debug("Deploy contract {} with fee {}", contractDefinition, fee);
    return trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getContractOperation().deploy(getAccount(),
              contractDefinition, nonce, fee);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    }).adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation) {
    return execute(contractInvocation, Fee.ZERO);
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    logger.debug("Execute contract {} with fee {}", contractInvocation, fee);
    return (ContractTxHash) trier.request(new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(final Long nonce) {
        try {
          return getAergoClient().getContractOperation().execute(getAccount(),
              contractInvocation, nonce, fee);
        } catch (Exception e) {
          throw exceptionConverter.convert(e);
        }
      }
    }).adapt(ContractTxHash.class);
  }

}
