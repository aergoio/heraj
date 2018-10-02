/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountAsyncOperation;
import hera.api.AccountEitherOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class AccountEitherTemplate implements AccountEitherOperation {

  protected final AccountAsyncOperation accountAsyncOperation;

  public AccountEitherTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public AccountEitherTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new AccountAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<List<Account>> list() {
    try {
      return accountAsyncOperation.list().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Account> create(String password) {
    try {
      return accountAsyncOperation.create(password).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Account> get(AccountAddress address) {
    try {
      return accountAsyncOperation.get(address).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> lock(final Authentication authentication) {
    try {
      return accountAsyncOperation.lock(authentication).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> unlock(final Authentication authentication) {
    try {
      return accountAsyncOperation.unlock(authentication).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    try {
      return accountAsyncOperation.importKey(encryptedKey, oldPassword, newPassword).get(TIMEOUT,
          TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<EncryptedPrivateKey> exportKey(final Authentication authentication) {

    try {
      return accountAsyncOperation.exportKey(authentication).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }
}
