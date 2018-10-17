/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChainer;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.AuthenticationConverterFactory;
import hera.transport.EncryptedPrivateKeyConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.AccountOuterClass.AccountList;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class AccountAsyncTemplate implements AccountAsyncOperation, ChannelInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> encryptedPkConverter =
      new EncryptedPrivateKeyConverterFactory().create();

  protected final ModelConverter<Account, AccountOuterClass.Account> accountConverter =
      new AccountConverterFactory().create();

  protected final ModelConverter<AccountState, Blockchain.State> accountStateConverter =
      new AccountStateConverterFactory().create();

  protected final ModelConverter<Authentication, Rpc.Personal> authenticationConverter =
      new AuthenticationConverterFactory().create();

  @Setter
  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public ResultOrErrorFuture<List<Account>> list() {
    ResultOrErrorFuture<List<Account>> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountList> listenableFuture =
        aergoService.getAccounts(Rpc.Empty.newBuilder().build());
    FutureChainer<AccountList, List<Account>> callback =
        new FutureChainer<>(nextFuture, accountList -> accountList.getAccountsList().stream()
            .map(accountConverter::convertToDomainModel).collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Account> create(final String password) {
    ResultOrErrorFuture<Account> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.Personal personal = Rpc.Personal.newBuilder().setPassphrase(password).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.createAccount(personal);
    FutureChainer<AccountOuterClass.Account, Account> callback =
        new FutureChainer<>(nextFuture, account -> {
          Account domainAccount = accountConverter.convertToDomainModel(account);
          return domainAccount;
        });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<AccountState> getState(final AccountAddress address) {
    ResultOrErrorFuture<AccountState> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder()
        .setValue(accountAddressConverter.convertToRpcModel(address)).build();
    ListenableFuture<Blockchain.State> listenableFuture = aergoService.getState(bytes);
    FutureChainer<Blockchain.State, AccountState> callback =
        new FutureChainer<>(nextFuture, state -> {
          final AccountState accountState = accountStateConverter.convertToDomainModel(state);
          accountState.setAddress(address);
          return accountState;
        });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> unlock(final Authentication authentication) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.unlockAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChainer<AccountOuterClass.Account, Boolean> callback =
        new FutureChainer<>(nextFuture, account -> null != account.getAddress());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;

  }

  @Override
  public ResultOrErrorFuture<Boolean> lock(final Authentication authentication) {
    ResultOrErrorFuture<Boolean> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.lockAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChainer<AccountOuterClass.Account, Boolean> callback =
        new FutureChainer<>(nextFuture, account -> null != account.getAddress());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    ResultOrErrorFuture<Account> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.ImportFormat importFormat =
        Rpc.ImportFormat.newBuilder().setWif(encryptedPkConverter.convertToRpcModel(encryptedKey))
            .setOldpass(oldPassword).setNewpass(newPassword).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture =
        aergoService.importAccount(importFormat);
    FutureChainer<AccountOuterClass.Account, Account> callback = new FutureChainer<>(nextFuture,
        rpcAccount -> accountConverter.convertToDomainModel(rpcAccount));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    ResultOrErrorFuture<EncryptedPrivateKey> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    ListenableFuture<Rpc.SingleBytes> listenableFuture =
        aergoService.exportAccount(authenticationConverter.convertToRpcModel(authentication));
    FutureChainer<Rpc.SingleBytes, EncryptedPrivateKey> callback =
        new FutureChainer<>(nextFuture, sb -> encryptedPkConverter.convertToDomainModel(sb));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}
