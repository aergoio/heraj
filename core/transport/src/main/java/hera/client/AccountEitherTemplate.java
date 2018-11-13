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
import hera.exception.NoStrategyFoundException;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountEitherTemplate implements AccountEitherOperation, ChannelInjectable {

  protected Context context;

  protected AccountAsyncTemplate accountAsyncOperation = new AccountAsyncTemplate();

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
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.list()))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Account> create(final String password) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.create(password)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<AccountState> getState(final AccountAddress address) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.getState(address)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Boolean> lock(final Authentication authentication) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.lock(authentication)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Boolean> unlock(final Authentication authentication) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.unlock(authentication)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Signature> sign(final Account account, final Transaction transaction) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.sign(account, transaction)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Boolean> verify(final Account account, final Transaction transaction) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.verify(account, transaction)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Account> importKey(final EncryptedPrivateKey encryptedKey,
      final String oldPassword, final String newPassword) {
    return context.getStrategy(TimeoutStrategy.class).map(
        f -> f.submit(accountAsyncOperation.importKey(encryptedKey, oldPassword, newPassword)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<EncryptedPrivateKey> exportKey(final Authentication authentication) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(accountAsyncOperation.exportKey(authentication)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

}
