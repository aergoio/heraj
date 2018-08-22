/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.FutureChainer;
import hera.api.AccountAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.AccountConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AccountOuterClass;
import types.AccountOuterClass.AccountList;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain.State;
import types.Rpc.Empty;
import types.Rpc.Personal;
import types.Rpc.SingleBytes;

@RequiredArgsConstructor
public class AccountAsyncTemplate implements AccountAsyncOperation {

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<Account, AccountOuterClass.Account> accountConverter;

  protected final ModelConverter<AccountState, State> accountStateConverter;

  public AccountAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public AccountAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new AccountConverterFactory().create(),
        new AccountStateConverterFactory().create());
  }

  @Override
  public ResultOrErrorFuture<List<Account>> list() {
    ResultOrErrorFuture<List<Account>> nextFuture = new ResultOrErrorFuture<>();

    ListenableFuture<AccountList> listenableFuture = aergoService
        .getAccounts(Empty.newBuilder().build());
    FutureChainer<AccountList, List<Account>> callback = new FutureChainer<>(nextFuture,
        accountList -> accountList.getAccountsList().stream()
            .map(accountConverter::convertToDomainModel)
            .collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Account> create(String password) {
    ResultOrErrorFuture<Account> nextFuture = new ResultOrErrorFuture<>();

    final Personal personal = Personal.newBuilder().setPassphrase(password).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture = aergoService
        .createAccount(personal);
    FutureChainer<AccountOuterClass.Account, Account> callback = new FutureChainer<>(nextFuture,
        account -> {
          Account domainAccount = accountConverter.convertToDomainModel(account);
          domainAccount.setPassword(password);
          return domainAccount;
        });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Account> get(AccountAddress address) {
    // TODO : replace with getState
    return list().thenApply(list -> list.stream()
        .filter(account -> address.equals(account.getAddress()))
        .findFirst().get());
  }

  @Override
  public ResultOrErrorFuture<Boolean> unlock(AccountAddress address, String password) {
    ResultOrErrorFuture<Boolean> nextFuture = new ResultOrErrorFuture<>();

    final Account domainAccount = Account.of(address, password);
    final Personal rpcPersonal = Personal.newBuilder()
        .setAccount(accountConverter.convertToRpcModel(domainAccount))
        .setPassphrase(domainAccount.getPassword()).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture = aergoService
        .unlockAccount(rpcPersonal);
    FutureChainer<AccountOuterClass.Account, Boolean> callback = new FutureChainer<>(nextFuture,
        account -> null != account.getAddress());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;

  }

  @Override
  public ResultOrErrorFuture<Boolean> lock(AccountAddress address, String password) {
    ResultOrErrorFuture<Boolean> nextFuture = new ResultOrErrorFuture<>();

    final Account domainAccount = Account.of(address, password);
    final Personal rpcPersonal = Personal.newBuilder()
        .setAccount(accountConverter.convertToRpcModel(domainAccount))
        .setPassphrase(domainAccount.getPassword()).build();
    ListenableFuture<AccountOuterClass.Account> listenableFuture = aergoService
        .lockAccount(rpcPersonal);
    FutureChainer<AccountOuterClass.Account, Boolean> callback = new FutureChainer<>(nextFuture,
        account -> null != account.getAddress());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<AccountState> getState(AccountAddress address) {
    ResultOrErrorFuture<AccountState> nextFuture = new ResultOrErrorFuture<>();

    final ByteString byteString = copyFrom(address.getValue());
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<State> listenableFuture = aergoService.getState(bytes);
    FutureChainer<State, AccountState> callback = new FutureChainer<>(nextFuture,
        state -> accountStateConverter.convertToDomainModel(state));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}
