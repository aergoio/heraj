/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountTemplate implements AccountOperation, ChannelInjectable {

  protected Context context;

  protected AccountEitherTemplate accountEitherOperation = new AccountEitherTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    accountEitherOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    accountEitherOperation.injectChannel(channel);
  }

  @Override
  public List<AccountAddress> list() {
    return accountEitherOperation.list().getResult();
  }

  @Override
  public ServerManagedAccount create(final String password) {
    return accountEitherOperation.create(password).getResult();
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return accountEitherOperation.getState(address).getResult();
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
  public Signature sign(final Account account, final Transaction transaction) {
    return accountEitherOperation.sign(account, transaction).getResult();
  }

  @Override
  public boolean verify(final Account account, final Transaction transaction) {
    return accountEitherOperation.verify(account, transaction).getResult();
  }

  @Override
  public ServerManagedAccount importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return accountEitherOperation.importKey(encryptedKey, oldPassword, newPassword).getResult();
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return accountEitherOperation.exportKey(authentication).getResult();
  }

}
