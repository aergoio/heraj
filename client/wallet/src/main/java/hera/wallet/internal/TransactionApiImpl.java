/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

import hera.api.function.Function0;
import hera.api.function.Function2;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.TryCountAndInterval;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.exception.WalletExceptionConverter;
import hera.key.Signer;
import hera.util.ExceptionConverter;
import hera.wallet.TransactionApi;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.NonNull;
import lombok.Setter;

public class TransactionApiImpl implements TransactionApi, ClientInjectable {

  protected final Trier trier;

  protected final Signer signer;

  @Setter
  @NonNull
  protected AergoClient client;

  protected final ExceptionConverter<WalletException> converter = new WalletExceptionConverter();

  /**
   * TransactionApiImpl constructor.
   * 
   * @param tryCountAndInterval a tryCountAndInterval
   * @param signer a signe
   */
  public TransactionApiImpl(final TryCountAndInterval tryCountAndInterval, final Signer signer) {
    final Trier trier = new Trier();
    trier.setTryCountAndInterval(tryCountAndInterval);
    trier.setNonceProvider(new SimpleNonceProvider());
    trier.setClientProvider(new Function0<AergoClient>() {

      @Override
      public AergoClient apply() {
        return client;
      }
    });
    this.trier = trier;

    this.signer = signer;
  }

  @Override
  public TxHash createName(final String name) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().createName(getSigner(), name, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash updateName(final String name, final AccountAddress newOwner) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().updateName(getSigner(), name, newOwner, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash stake(final Aer amount) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().stake(getSigner(), amount, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash unstake(final Aer amount) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().unstake(getSigner(), amount, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash voteBp(List<String> candidates) {
    return vote("voteBP", candidates);
  }


  @Override
  public TxHash vote(final String voteId, final List<String> candidates) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().vote(getSigner(), voteId, candidates, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          final RawTransaction rawTransaction = RawTransaction.newBuilder()
              .chainIdHash(getClient().getCachedChainIdHash())
              .from(getSigner().getPrincipal())
              .to(recipient)
              .amount(amount)
              .nonce(t)
              .fee(fee)
              .payload(payload)
              .build();
          return getClient().getTransactionOperation().commit(getSigner().sign(rawTransaction));
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          final RawTransaction rawTransaction = RawTransaction.newBuilder()
              .chainIdHash(getClient().getCachedChainIdHash())
              .from(getSigner().getPrincipal())
              .to(recipient)
              .amount(amount)
              .nonce(t)
              .fee(fee)
              .payload(payload)
              .build();
          return getClient().getTransactionOperation().commit(getSigner().sign(rawTransaction));
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        protected AtomicBoolean isFirst = new AtomicBoolean(true);

        @Override
        public TxHash apply(Signer signer, Long t) {
          if (isFirst.get()) {
            isFirst.set(false);
            return getClient().getTransactionOperation().commit(getSigner().sign(rawTransaction));
          } else {
            final RawTransaction withNewNonce = rawTransaction.withNonce(t);
            return getClient().getTransactionOperation().commit(getSigner().sign(withNewNonce));
          }
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        protected AtomicBoolean isFirst = new AtomicBoolean(true);

        @Override
        public TxHash apply(Signer signer, Long t) {
          if (isFirst.get()) {
            isFirst.set(false);
            return getClient().getTransactionOperation().commit(signedTransaction);
          } else {
            final RawTransaction rawTransaction = signedTransaction.getRawTransaction();
            final RawTransaction withNewNonce = rawTransaction.withNonce(t);
            return getClient().getTransactionOperation().commit(getSigner().sign(withNewNonce));
          }
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation().deploy(getSigner(), contractDefinition, t,
              fee);
        }
      }).adapt(ContractTxHash.class);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxHash redeploy(final ContractAddress existingContract,
      final ContractDefinition contractDefinition, final Fee fee) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation().redeploy(getSigner(), existingContract,
              contractDefinition, t, fee);
        }
      }).adapt(ContractTxHash.class);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    try {
      return trier.request(getSigner(), new Function2<Signer, Long, TxHash>() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation().execute(getSigner(), contractInvocation, t,
              fee);
        }
      }).adapt(ContractTxHash.class);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  protected Signer getSigner() {
    if (null == this.signer || null == this.signer.getPrincipal()) {
      throw new WalletException("Unlock account first");
    }
    return this.signer;
  }

  protected AergoClient getClient() {
    if (null == this.client) {
      throw new WalletException("Aergo client isn't binded yet");
    }
    return this.client;
  }

}
