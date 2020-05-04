/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.TxRequestFunction;
import hera.client.TxRequester;
import hera.key.Signer;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class TransactionApiImpl extends AbstractApi implements TransactionApi {

  @NonNull
  protected final ClientProvider clientProvider;
  @NonNull
  protected final Signer signer;
  @NonNull
  protected final TxRequester txRequester;

  @Override
  public TxHash createName(final String name) {
    return createName(Name.of(name));
  }

  @Override
  public TxHash createName(final Name name) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().createNameTx(signer, name, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash updateName(final String name, final AccountAddress newOwner) {
    return updateName(Name.of(name), newOwner);
  }

  @Override
  public TxHash updateName(final Name name, final AccountAddress newOwner) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().updateNameTx(signer, name, newOwner, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash stake(final Aer amount) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().stakeTx(signer, amount, t);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash unstake(final Aer amount) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().unstakeTx(signer, amount, t);
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
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getAccountOperation().voteTx(signer, voteId, candidates, t);
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
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getTransactionOperation()
              .sendTx(signer, recipient, amount, t, fee, payload);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee) {
    return send(Name.of(recipient), amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final String recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    return send(Name.of(recipient), amount, fee, payload);
  }

  @Override
  public TxHash send(final Name recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final Name recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getTransactionOperation()
              .sendTx(signer, recipient, amount, t, fee, payload);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getTransactionOperation().commit(signer.sign(rawTransaction));
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getTransactionOperation().commit(signedTransaction);
        }
      });
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public TxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation().deployTx(signer, contractDefinition, t,
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
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public ContractTxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation()
              .redeployTx(signer, existingContract, contractDefinition, t, fee);
        }
      }).adapt(ContractTxHash.class);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    try {
      return txRequester.request(getClient(), getSigner(), new TxRequestFunction() {

        @Override
        public ContractTxHash apply(Signer signer, Long t) {
          return getClient().getContractOperation().executeTx(signer, contractInvocation, t, fee);
        }
      }).adapt(ContractTxHash.class);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  protected AergoClient getClient() {
    return this.clientProvider.getClient();
  }

  protected Signer getSigner() {
    return this.signer;
  }

}
