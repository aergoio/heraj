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
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.List;
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
  public ResultOrError<List<Account>> list() {
    try {
      return accountAsyncOperation.list().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Account> create(String password) {
    try {
      return accountAsyncOperation.create(password).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Account> get(AccountAddress address) {
    try {
      return accountAsyncOperation.get(address).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Boolean> lock(AccountAddress address, String password) {
    try {
      return accountAsyncOperation.lock(address, password).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Boolean> unlock(AccountAddress address, String password) {
    try {
      return accountAsyncOperation.unlock(address, password).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<AccountState> getState(AccountAddress address) {
    try {
      return accountAsyncOperation.getState(address).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}
