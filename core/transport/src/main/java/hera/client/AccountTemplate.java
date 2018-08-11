/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.AccountAsyncOperation;
import hera.api.AccountOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@RequiredArgsConstructor
public class AccountTemplate implements AccountOperation {

  protected final AccountAsyncOperation accountAsyncOperation;

  public AccountTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public AccountTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new AccountAsyncTemplate(aergoService));
  }

  @Override
  public List<Account> list() {
    try {
      return accountAsyncOperation.list().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Account create(String password) {
    try {
      return accountAsyncOperation.create(password).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Optional<Account> get(AccountAddress address) {
    try {
      return accountAsyncOperation.get(address).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean lock(Account domainAccount) {
    try {
      return accountAsyncOperation.lock(domainAccount).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public boolean unlock(Account domainAccount) {
    try {
      return accountAsyncOperation.unlock(domainAccount).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Optional<AccountState> getState(AccountAddress address) {
    try {
      return accountAsyncOperation.getState(address).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}
