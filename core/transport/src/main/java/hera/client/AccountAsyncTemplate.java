/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.base.Suppliers.memoize;
import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.KeyHoldable;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.RpcException;
import hera.exception.TransactionVerificationException;
import hera.strategy.StrategyChain;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class AccountAsyncTemplate
    implements AccountAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> encryptedPkConverter =
      new EncryptedPrivateKeyConverterFactory().create();

  protected final ModelConverter<ServerManagedAccount, AccountOuterClass.Account> accountConverter =
      new AccountConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<Authentication, Rpc.Personal> authenticationConverter =
      new AuthenticationConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  private final Function0<ResultOrErrorFuture<List<AccountAddress>>> listFunction =
      () -> {
        ResultOrErrorFuture<List<AccountAddress>> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        ListenableFuture<AccountOuterClass.AccountList> listenableFuture =
            aergoService.getAccounts(Rpc.Empty.newBuilder().build());
        FutureChain<AccountOuterClass.AccountList, List<AccountAddress>> callback =
            new FutureChain<>(nextFuture);
        callback.setSuccessHandler(list -> of(
            () -> list.getAccountsList().stream().map(accountConverter::convertToDomainModel)
                .map(Account::getAddress).collect(toList())));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  private final Function1<String, ResultOrErrorFuture<Account>> createFunction =
      (password) -> {
        ResultOrErrorFuture<Account> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

        Rpc.Personal personal = Rpc.Personal.newBuilder().setPassphrase(password).build();
        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService.createAccount(personal);
        FutureChain<AccountOuterClass.Account, Account> callback = new FutureChain<>(nextFuture);
        callback.setSuccessHandler(
            account -> of(() -> accountConverter.convertToDomainModel(account)));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  private final Function1<AccountAddress,
      ResultOrErrorFuture<AccountState>> getStateFunction =
          (address) -> {
            ResultOrErrorFuture<AccountState> nextFuture =
                ResultOrErrorFutureFactory.supplyEmptyFuture();

            Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder()
                .setValue(accountAddressConverter.convertToRpcModel(address)).build();
            ListenableFuture<Blockchain.State> listenableFuture = aergoService.getState(bytes);
            FutureChain<Blockchain.State, AccountState> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(state -> of(() -> {
              AccountState accountState = accountStateConverter.convertToDomainModel(state);
              accountState.setAddress(address);
              return accountState;
            }));
            addCallback(listenableFuture, callback, directExecutor());

            return nextFuture;
          };

  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> unlockFunction =
      (authentication) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService
                .unlockAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<AccountOuterClass.Account, Boolean> callback =
            new FutureChain<>(nextFuture);
        callback.setSuccessHandler(account -> of(() -> null != account.getAddress()));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  private final Function2<Account, Transaction, ResultOrErrorFuture<Signature>> signFunction =
      (account, transaction) -> {
        ResultOrErrorFuture<Signature> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        if (account instanceof KeyHoldable) {
          KeyHoldable keyHoldable = (KeyHoldable) account;
          Transaction copy = Transaction.copyOf(transaction);
          BytesValue signature =
              keyHoldable.sign(copy.calculateHash().getBytesValue().get());
          copy.setSignature(Signature.of(signature, null));
          nextFuture.complete(success(Signature.of(signature, copy.calculateHash())));
        } else {
          Blockchain.Tx rpcTransaction =
              transactionConverter.convertToRpcModel(transaction);
          ListenableFuture<Blockchain.Tx> listenableFuture =
              aergoService.signTX(rpcTransaction);
          FutureChain<Blockchain.Tx, Signature> callback = new FutureChain<>(nextFuture);
          callback.setSuccessHandler(tx -> of(() -> {
            BytesValue sign =
                ofNullable(tx.getBody().getSign()).map(ByteString::toByteArray)
                    .filter(bytes -> 0 != bytes.length).map(BytesValue::of)
                    .orElseThrow(() -> new RpcException(
                        "Signing failed: sign field is not found at sign result"));
            TxHash hash = ofNullable(tx.getHash()).map(ByteString::toByteArray)
                .filter(bytes -> 0 != bytes.length).map(BytesValue::new).map(TxHash::new)
                .orElseThrow(() -> new RpcException(
                    "Signing failed: txHash field is not found at sign result"));
            return Signature.of(sign, hash);
          }));
          addCallback(listenableFuture, callback, directExecutor());
        }

        return nextFuture;
      };

  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      (account, transaction) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        if (account instanceof KeyHoldable) {
          KeyHoldable keyHoldable = (KeyHoldable) account;
          Transaction copy = Transaction.copyOf(transaction);
          BytesValue signature = copy.getSignature().getSign();
          copy.setSignature(null);
          nextFuture.complete(
              success(keyHoldable.verify(copy.calculateHash().getBytesValue().get(), signature)));
        } else {
          Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
          ListenableFuture<Rpc.VerifyResult> listenableFuture = aergoService.verifyTX(tx);
          FutureChain<Rpc.VerifyResult, Boolean> callback =
              new FutureChain<Rpc.VerifyResult, Boolean>(nextFuture);
          callback.setSuccessHandler(result -> of(() -> Optional.of(result)
              .map(v -> Rpc.VerifyStatus.VERIFY_STATUS_OK == v.getError())
              .orElseThrow(() -> new TransactionVerificationException(result.getError()))));
          addCallback(listenableFuture, callback, directExecutor());
        }

        return nextFuture;
      };

  private final Function1<Authentication, ResultOrErrorFuture<Boolean>> lockFunction =
      (authentication) -> {
        ResultOrErrorFuture<Boolean> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        ListenableFuture<AccountOuterClass.Account> listenableFuture =
            aergoService.lockAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<AccountOuterClass.Account, Boolean> callback =
            new FutureChain<>(nextFuture);
        callback.setSuccessHandler(account -> of(() -> null != account.getAddress()));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  private final Function3<EncryptedPrivateKey, String, String,
      ResultOrErrorFuture<Account>> importKeyFunction =
          (encryptedKey, oldPassword, newPassword) -> {
            ResultOrErrorFuture<Account> nextFuture =
                ResultOrErrorFutureFactory.supplyEmptyFuture();

            Rpc.ImportFormat importFormat = Rpc.ImportFormat.newBuilder()
                .setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
                .setOldpass(oldPassword)
                .setNewpass(newPassword).build();
            ListenableFuture<AccountOuterClass.Account> listenableFuture =
                aergoService.importAccount(importFormat);
            FutureChain<AccountOuterClass.Account, Account> callback =
                new FutureChain<>(nextFuture);
            callback.setSuccessHandler(
                account -> of(() -> accountConverter.convertToDomainModel(account)));
            addCallback(listenableFuture, callback, directExecutor());

            return nextFuture;
          };

  private final Function1<Authentication,
      ResultOrErrorFuture<EncryptedPrivateKey>> exportKeyFunction = (authentication) -> {
        ResultOrErrorFuture<EncryptedPrivateKey> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        ListenableFuture<Rpc.SingleBytes> listenableFuture =
            aergoService
                .exportAccount(authenticationConverter.convertToRpcModel(authentication));
        FutureChain<Rpc.SingleBytes, EncryptedPrivateKey> callback =
            new FutureChain<>(nextFuture);
        callback.setSuccessHandler(
            rawPk -> of(() -> encryptedPkConverter.convertToDomainModel(rawPk)));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  protected Supplier<
      Function0<ResultOrErrorFuture<List<AccountAddress>>>> wrappedListFunctionSupplier =
          memoize(() -> getStrategyChain().apply(listFunction));

  protected Supplier<
      Function1<String, ResultOrErrorFuture<Account>>> createFunctionSupplier =
          memoize(() -> getStrategyChain().apply(createFunction));

  protected Supplier<Function1<AccountAddress,
      ResultOrErrorFuture<AccountState>>> getStateFunctionSupplier =
          memoize(() -> getStrategyChain().apply(getStateFunction));

  protected Supplier<
      Function1<Authentication, ResultOrErrorFuture<Boolean>>> unlockFunctionSupplier =
          memoize(() -> getStrategyChain().apply(unlockFunction));

  protected Supplier<Function2<Account, Transaction,
      ResultOrErrorFuture<Signature>>> signFunctionSupplier =
          memoize(() -> getStrategyChain().apply(signFunction));

  protected Supplier<Function2<Account, Transaction,
      ResultOrErrorFuture<Boolean>>> verifyFunctionSupplier =
          memoize(() -> getStrategyChain().apply(verifyFunction));

  protected Supplier<
      Function1<Authentication, ResultOrErrorFuture<Boolean>>> lockFunctionSupplier =
          memoize(() -> getStrategyChain().apply(lockFunction));

  protected Supplier<Function3<EncryptedPrivateKey, String, String,
      ResultOrErrorFuture<Account>>> importKeyFunctionSupplier =
          memoize(() -> getStrategyChain().apply(importKeyFunction));

  protected Supplier<Function1<Authentication,
      ResultOrErrorFuture<EncryptedPrivateKey>>> exportKeyFunctionSupplier =
          memoize(() -> getStrategyChain().apply(exportKeyFunction));

  @Override
  public ResultOrErrorFuture<List<AccountAddress>> list() {
    return wrappedListFunctionSupplier.get().apply();
  }

  @Override
  public ResultOrErrorFuture<Account> create(final String password) {
    return createFunctionSupplier.get().apply(password);
  }

  @Override
  public ResultOrErrorFuture<AccountState> getState(final AccountAddress address) {
    return getStateFunctionSupplier.get().apply(address);
  }

  @Override
  public ResultOrErrorFuture<Boolean> lock(final Authentication authentication) {
    return lockFunctionSupplier.get().apply(authentication);
  }

  @Override
  public ResultOrErrorFuture<Boolean> unlock(final Authentication authentication) {
    return unlockFunctionSupplier.get().apply(authentication);
  }

  @Override
  public ResultOrErrorFuture<Signature> sign(final Account account, final Transaction transaction) {
    return signFunctionSupplier.get().apply(account, transaction);
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(final Account account, final Transaction transaction) {
    return verifyFunctionSupplier.get().apply(account, transaction);
  }

  @Override
  public ResultOrErrorFuture<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return importKeyFunctionSupplier.get().apply(encryptedKey, oldPassword, newPassword);
  }

  @Override
  public ResultOrErrorFuture<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    return exportKeyFunctionSupplier.get().apply(authentication);
  }

}
