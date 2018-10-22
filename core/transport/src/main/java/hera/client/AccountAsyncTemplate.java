/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChain;
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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.RpcException;
import hera.exception.TransactionVerificationException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AccountOuterClass.AccountList;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Blockchain.State;
import types.Blockchain.Tx;
import types.Rpc;
import types.Rpc.SingleBytes;
import types.Rpc.VerifyResult;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class AccountAsyncTemplate implements AccountAsyncOperation, ChannelInjectable {

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

  @Setter
  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public ResultOrErrorFuture<List<AccountAddress>> list() {
    ResultOrErrorFuture<List<AccountAddress>> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountList> listenableFuture =
        aergoService.getAccounts(Rpc.Empty.newBuilder().build());
    FutureChain<AccountList, List<AccountAddress>> callback = new FutureChain<>(nextFuture,
        accountList -> accountList.getAccountsList().stream()
            .map(accountConverter::convertToDomainModel).map(Account::getAddress)
            .collect(toList()));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ServerManagedAccount> create(final String password) {
    ResultOrErrorFuture<ServerManagedAccount> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.Personal personal = Rpc.Personal.newBuilder().setPassphrase(password).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.createAccount(personal);
    FutureChain<AccountOuterClass.Account, ServerManagedAccount> callback =
        new FutureChain<>(nextFuture, accountConverter::convertToDomainModel);
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<AccountState> getState(final AccountAddress address) {
    ResultOrErrorFuture<AccountState> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder()
        .setValue(accountAddressConverter.convertToRpcModel(address)).build();
    ListenableFuture<Blockchain.State> listenableFuture = aergoService.getState(bytes);
    FutureChain<State, AccountState> callback =
        new FutureChain<>(nextFuture, state -> {
          final AccountState accountState = accountStateConverter.convertToDomainModel(state);
          accountState.setAddress(address);
          return accountState;
        });
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> unlock(final Authentication authentication) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.unlockAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChain<AccountOuterClass.Account, Boolean> callback =
        new FutureChain<>(nextFuture, account -> null != account.getAddress());
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Signature> sign(final Account account, final Transaction transaction) {
    ResultOrErrorFuture<Signature> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    if (account instanceof KeyHoldable) {
      final KeyHoldable keyHoldable = (KeyHoldable) account;
      final Transaction copy = Transaction.copyOf(transaction);
      final BytesValue signature = keyHoldable.sign(copy.calculateHash().getBytesValue().get());
      copy.setSignature(Signature.of(signature, null));
      nextFuture.complete(success(Signature.of(signature, copy.calculateHash())));
    } else {
      final Blockchain.Tx rpcTransaction = transactionConverter.convertToRpcModel(transaction);
      final ListenableFuture<Blockchain.Tx> listenableFuture = aergoService.signTX(rpcTransaction);
      FutureChain<Tx, Signature> callback = new FutureChain<>(nextFuture, tx -> {
        final BytesValue sign = ofNullable(tx.getBody().getSign()).map(ByteString::toByteArray)
            .filter(bytes -> 0 != bytes.length).map(BytesValue::of).orElseThrow(
                () -> new RpcException("Signing failed: sign field is not found at sign result"));
        final TxHash hash = ofNullable(tx.getHash()).map(ByteString::toByteArray)
            .filter(bytes -> 0 != bytes.length).map(BytesValue::new).map(TxHash::new).orElseThrow(
                () -> new RpcException("Signing failed: txHash field is not found at sign result"));
        return Signature.of(sign, hash);
      });
      addCallback(listenableFuture, callback, directExecutor());
    }

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(Account account, Transaction transaction) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    if (account instanceof KeyHoldable) {
      final KeyHoldable keyHoldable = (KeyHoldable) account;
      final Transaction copy = Transaction.copyOf(transaction);
      final BytesValue signature = copy.getSignature().getSign();
      copy.setSignature(null);
      nextFuture.complete(
          success(keyHoldable.verify(copy.calculateHash().getBytesValue().get(), signature)));
    } else {
      final Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
      ListenableFuture<Rpc.VerifyResult> listenableFuture = aergoService.verifyTX(tx);
      FutureChain<VerifyResult, Boolean> callback =
          new FutureChain<VerifyResult, Boolean>(nextFuture,
              verifyResult -> Rpc.VerifyStatus.VERIFY_STATUS_OK == verifyResult.getError()) {
            @Override
            public void onSuccess(Rpc.VerifyResult t) {
              if (Rpc.VerifyStatus.VERIFY_STATUS_OK == t.getError()) {
                super.onSuccess(t);
              } else {
                super.onFailure(new TransactionVerificationException(t.getError()));
              }
            }
          };
      addCallback(listenableFuture, callback, directExecutor());
    }

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> lock(final Authentication authentication) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.lockAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChain<AccountOuterClass.Account, Boolean> callback =
        new FutureChain<>(nextFuture, account -> null != account.getAddress());
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ServerManagedAccount> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    ResultOrErrorFuture<ServerManagedAccount> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.ImportFormat importFormat =
        Rpc.ImportFormat.newBuilder().setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
            .setOldpass(oldPassword).setNewpass(newPassword).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.importAccount(importFormat);
    FutureChain<AccountOuterClass.Account, ServerManagedAccount> callback = new FutureChain<>(
        nextFuture, accountConverter::convertToDomainModel);
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    ResultOrErrorFuture<EncryptedPrivateKey> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<Rpc.SingleBytes> listenableFuture =
        aergoService.exportAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChain<SingleBytes, EncryptedPrivateKey> callback =
        new FutureChain<>(nextFuture, encryptedPkConverter::convertToDomainModel);
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }


}
