/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountEitherOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountEitherTemplate implements AccountEitherOperation, ChannelInjectable {

  protected AccountAsyncTemplate accountAsyncOperation = new AccountAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    accountAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    accountAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<List<AccountAddress>> list() {
    return accountAsyncOperation.list().get();
  }

  @Override
  public ResultOrError<Account> create(final String password) {
    return accountAsyncOperation.create(password).get();
  }

  @Override
  public ResultOrError<AccountState> getState(final AccountAddress address) {
    return accountAsyncOperation.getState(address).get();
  }

  @Override
  public ResultOrError<Boolean> lock(final Authentication authentication) {
    return accountAsyncOperation.lock(authentication).get();
  }

  @Override
  public ResultOrError<Boolean> unlock(final Authentication authentication) {
    return accountAsyncOperation.unlock(authentication).get();
  }

  @Override
  public ResultOrError<Signature> sign(final Account account, final Transaction transaction) {
    return accountAsyncOperation.sign(account, transaction).get();
  }

  @Override
  public ResultOrError<Boolean> verify(final Account account, final Transaction transaction) {
    return accountAsyncOperation.verify(account, transaction).get();
  }

  @Override
  public ResultOrError<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return accountAsyncOperation.importKey(encryptedKey, oldPassword, newPassword).get();
  }

  @Override
  public ResultOrError<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    return accountAsyncOperation.exportKey(authentication).get();
  }

}
