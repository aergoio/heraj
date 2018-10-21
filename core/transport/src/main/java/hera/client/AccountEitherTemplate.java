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
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Time;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountEitherTemplate implements AccountEitherOperation, ChannelInjectable {

  protected Context context;

  protected AccountAsyncTemplate accountAsyncOperation = new AccountAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout).get();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    accountAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    accountAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<List<AccountAddress>> list() {
    return accountAsyncOperation.list().get(getTimeout().getValue(), getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ServerManagedAccount> create(final String password) {
    return accountAsyncOperation.create(password).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<AccountState> getState(final AccountAddress address) {
    return accountAsyncOperation.getState(address).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<Boolean> lock(final Authentication authentication) {
    return accountAsyncOperation.lock(authentication).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<Boolean> unlock(final Authentication authentication) {
    return accountAsyncOperation.unlock(authentication).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<Signature> sign(final Account account, final Transaction transaction) {
    return accountAsyncOperation.sign(account, transaction).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<Boolean> verify(final Account account, final Transaction transaction) {
    return accountAsyncOperation.verify(account, transaction).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<ServerManagedAccount> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return accountAsyncOperation.importKey(encryptedKey, oldPassword, newPassword)
        .get(getTimeout().getValue(), getTimeout().getUnit());
  }

  @Override
  public ResultOrError<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    return accountAsyncOperation.exportKey(authentication).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

}
