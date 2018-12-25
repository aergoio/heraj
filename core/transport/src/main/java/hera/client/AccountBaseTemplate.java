/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.api.tupleorerror.FunctionChain.success;
import static hera.util.ArrayUtils.concat;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.GovernanceRecipient;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.TransactionVerificationException;
import hera.key.Signer;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.Name;
import types.Rpc.NameInfo;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class AccountBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    transactionBaseTemplate.setContextProvider(contextProvider);
  }

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    transactionBaseTemplate.setChannel(channel);
  }

  @Getter
  private final Function1<AccountAddress, ResultOrErrorFuture<AccountState>> stateFunction =
      (address) -> {
        ResultOrErrorFuture<AccountState> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("GetState with {}", address);

        Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder()
            .setValue(accountAddressConverter.convertToRpcModel(address)).build();
        ListenableFuture<Blockchain.State> listenableFuture = aergoService.getState(bytes);
        FutureChain<Blockchain.State, AccountState> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(state -> of(() -> {
          final AccountState withoutAddress = accountStateConverter.convertToDomainModel(state);
          return new AccountState(address, withoutAddress.getNonce(), withoutAddress.getBalance());
        }));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function3<Account, String, Long, ResultOrErrorFuture<TxHash>> createNameFunction =
      (account, name, nonce) -> {
        try {
          logger.debug("Create account name to account: {}, name: {}, nonce: {}",
              account.getAddress(), name, nonce);

          final BytesValue payload = new BytesValue(("c" + name).getBytes());
          final RawTransaction rawTransaction = new RawTransaction(account.getAddress(),
              GovernanceRecipient.AERGO_NAME,
              null,
              nonce,
              Fee.of(null, 0),
              payload,
              Transaction.TxType.GOVERNANCE);
          final Transaction signed =
              getSignFunction().apply(account, rawTransaction).get().getResult();
          return transactionBaseTemplate.getCommitFunction().apply(signed);
        } catch (Exception e) {
          final ResultOrErrorFuture<TxHash> future = ResultOrErrorFutureFactory.supplyEmptyFuture();
          future.complete(fail(e));
          return future;
        }
      };

  @Getter
  private final Function4<Account, String, AccountAddress, Long,
      ResultOrErrorFuture<TxHash>> updateNameFunction =
          (owner, name, newOwner, nonce) -> {
            try {
              logger.debug(
                  "Update account name from account: {}, name: {}, to account: {}, nonce: {}",
                  owner.getAddress(), name, newOwner, nonce);
              final BytesValue payload = new BytesValue(concat(("u" + name + ",").getBytes(),
                  accountAddressConverter.convertToRpcModel(newOwner).toByteArray()));
              final RawTransaction rawTransaction = new RawTransaction(owner.getAddress(),
                  GovernanceRecipient.AERGO_NAME,
                  null,
                  nonce,
                  Fee.of(null, 0),
                  payload,
                  Transaction.TxType.GOVERNANCE);
              final Transaction signed =
                  getSignFunction().apply(owner, rawTransaction).get().getResult();
              return transactionBaseTemplate.getCommitFunction().apply(signed);
            } catch (Exception e) {
              final ResultOrErrorFuture<TxHash> future =
                  ResultOrErrorFutureFactory.supplyEmptyFuture();
              future.complete(fail(e));
              return future;
            }
          };

  @Getter
  private final Function1<String, ResultOrErrorFuture<AccountAddress>> getNameOwnerFunction =
      (name) -> {
        ResultOrErrorFuture<AccountAddress> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Get name owner of name: {}", name);

        final Name rpcName = Name.newBuilder().setName(name).build();
        ListenableFuture<NameInfo> listenableFuture = aergoService.getNameInfo(rpcName);
        FutureChain<NameInfo, AccountAddress> callback =
            new FutureChain<>(nextFuture, contextProvider.get());
        callback.setSuccessHandler(nameInfo -> of(
            () -> accountAddressConverter.convertToDomainModel(nameInfo.getOwner())));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function2<Account, RawTransaction,
      ResultOrErrorFuture<Transaction>> signFunction =
          (account, rawTransaction) -> {
            ResultOrErrorFuture<Transaction> nextFuture =
                ResultOrErrorFutureFactory.supplyEmptyFuture();
            logger.debug("Sign request with account: {}, rawTx: {}", account, rawTransaction);

            if (account instanceof Signer) {
              Signer signer = (Signer) account;

              final Transaction signed = signer.sign(rawTransaction);
              nextFuture.complete(success(signed));
            } else {
              final Transaction domainTransaction =
                  new Transaction(rawTransaction, null, null, null, 0, false);
              Blockchain.Tx rpcTransaction =
                  transactionConverter.convertToRpcModel(domainTransaction);
              ListenableFuture<Blockchain.Tx> listenableFuture =
                  aergoService.signTX(rpcTransaction);
              FutureChain<Blockchain.Tx, Transaction> callback =
                  new FutureChain<>(nextFuture, contextProvider.get());
              callback
                  .setSuccessHandler(tx -> of(() -> transactionConverter.convertToDomainModel(tx)));
              addCallback(listenableFuture, callback, directExecutor());
            }

            return nextFuture;
          };

  @Getter
  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      (account, transaction) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("Sign verify with account: {}, signedTx: {}", account, transaction);

        if (account instanceof Signer) {
          Signer signer = (Signer) account;
          final boolean verifyResult = signer.verify(transaction);
          nextFuture.complete(success(verifyResult));
        } else {
          Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
          ListenableFuture<Rpc.VerifyResult> listenableFuture = aergoService.verifyTX(tx);
          FutureChain<Rpc.VerifyResult, Boolean> callback =
              new FutureChain<Rpc.VerifyResult, Boolean>(nextFuture, contextProvider.get());
          callback.setSuccessHandler(result -> of(() -> Optional.of(result)
              .map(v -> Rpc.VerifyStatus.VERIFY_STATUS_OK == v.getError())
              .orElseThrow(() -> new TransactionVerificationException(result.getError()))));
          addCallback(listenableFuture, callback, directExecutor());
        }

        return nextFuture;
      };

}
