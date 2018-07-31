/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import com.google.protobuf.ByteString;
import hera.api.AccountOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.transport.AccountConverterFactory;
import hera.transport.AccountStateConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import types.AccountOuterClass;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain.State;
import types.Rpc.Empty;
import types.Rpc.Personal;
import types.Rpc.SingleBytes;

@RequiredArgsConstructor
public class AccountTemplate implements AccountOperation {

  protected final AergoRPCServiceBlockingStub aergoService;

  protected final ModelConverter<Account, AccountOuterClass.Account> accountConverter;

  protected final ModelConverter<AccountState, State> accountStateConverter;

  public AccountTemplate(final ManagedChannel channel) {
    this(newBlockingStub(channel));
  }

  public AccountTemplate(final AergoRPCServiceBlockingStub aergoService) {
    this(aergoService, new AccountConverterFactory().create(),
        new AccountStateConverterFactory().create());
  }

  @Override
  public List<Account> list() {
    return aergoService
        .getAccounts(Empty.newBuilder().build())
        .getAccountsList().stream()
        .map(accountConverter::convertToDomainModel)
        .collect(toList());
  }

  @Override
  public Optional<Account> get(final AccountAddress address) {
    if (null == address) {
      return empty();
    }
    return list().stream()
        .filter(account -> address.equals(account.getAddress()))
        .findFirst();
  }

  @Override
  public Account create(final String password) {
    final Personal personal = Personal.newBuilder().setPassphrase(password).build();
    final AccountOuterClass.Account account = aergoService.createAccount(personal);
    final Account domainAccount = accountConverter.convertToDomainModel(account);
    domainAccount.setPassword(password);
    return domainAccount;
  }


  @Override
  public boolean unlock(final Account domainAccount) {
    final AccountOuterClass.Account rpcAccount = accountConverter.convertToRpcModel(domainAccount);
    final Personal rpcPersonal = Personal.newBuilder()
        .setAccount(rpcAccount)
        .setPassphrase(domainAccount.getPassword()).build();
    final AccountOuterClass.Account responseAccount = aergoService.unlockAccount(rpcPersonal);
    return null != responseAccount.getAddress();
  }


  @Override
  public boolean lock(final Account domainAccount) {
    final AccountOuterClass.Account rpcAccount = accountConverter.convertToRpcModel(domainAccount);
    final Personal rpcPersonal = Personal.newBuilder()
        .setAccount(rpcAccount)
        .setPassphrase(domainAccount.getPassword()).build();
    final AccountOuterClass.Account responseAccount = aergoService.lockAccount(rpcPersonal);
    return null != responseAccount.getAddress();
  }

  @Override
  public Optional<AccountState> getState(final AccountAddress address) {
    try {
      final ByteString byteString = copyFrom(address.getValue());
      final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
      final State state = aergoService.getState(bytes);
      return ofNullable(accountStateConverter.convertToDomainModel(state));
    } catch (final StatusRuntimeException e) {
      if (ofNullable(e.getStatus()).map(Status::getCode)
          .filter(code -> Status.NOT_FOUND.getCode() == code).isPresent()) {
        return empty();
      }
      throw e;
    }
  }
}
