/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountEitherOperation;
import hera.api.AccountOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class AccountTemplate implements AccountOperation {

  protected final AccountEitherOperation accountEitherOperation;

  public AccountTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public AccountTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new AccountEitherTemplate(aergoService, context));
  }

  @Override
  public List<Account> list() {
    return accountEitherOperation.list().getResult();
  }

  @Override
  public Account create(final String password) {
    return accountEitherOperation.create(password).getResult();
  }

  @Override
  public Account get(final AccountAddress address) {
    return accountEitherOperation.get(address).getResult();
  }

  @Override
  public boolean lock(final Authentication authentication) {
    ResultOrError<Boolean> fuck = accountEitherOperation.lock(authentication);
    return fuck.getResult();
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    return accountEitherOperation.unlock(authentication).getResult();
  }

  @Override
  public Account importKey(final EncryptedPrivateKey encryptedKey, final String oldPassword,
      final String newPassword) {
    return accountEitherOperation.importKey(encryptedKey, oldPassword, newPassword).getResult();
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return accountEitherOperation.exportKey(authentication).getResult();
  }

}
