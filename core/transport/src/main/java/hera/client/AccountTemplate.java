/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountTemplate implements AccountOperation, ChannelInjectable, StrategyAcceptable {

  protected AccountEitherTemplate accountEitherOperation = new AccountEitherTemplate();

  @Override
  public void injectChannel(final ManagedChannel channel) {
    accountEitherOperation.injectChannel(channel);
  }

  @Override
  public void accept(final StrategyChain strategyChain) {
    accountEitherOperation.accept(strategyChain);
  }

  @Override
  public List<AccountAddress> list() {
    return accountEitherOperation.list().getResult();
  }

  @Override
  public Account create(final String password) {
    return accountEitherOperation.create(password).getResult();
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return accountEitherOperation.getState(address).getResult();
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return accountEitherOperation.lock(authentication).getResult();
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
  public Account importKey(final EncryptedPrivateKey encryptedKey, final String oldPassword,
      final String newPassword) {
    return accountEitherOperation.importKey(encryptedKey, oldPassword, newPassword).getResult();
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return accountEitherOperation.exportKey(authentication).getResult();
  }

}
