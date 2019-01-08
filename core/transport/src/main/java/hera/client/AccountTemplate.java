/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_SIGN;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME;
import static hera.TransportConstants.ACCOUNT_VERIFY;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountOperation;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountTemplate
    implements AccountOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getAccountBaseTemplate().setChannel(channel);;
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getAccountBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, FinishableFuture<AccountState>> stateFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getStateFunction(), ACCOUNT_GETSTATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Account, String, Long,
      FinishableFuture<TxHash>> createNameFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getCreateNameFunction(),
                  ACCOUNT_CREATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, String, AccountAddress, Long,
      FinishableFuture<TxHash>> updateNameFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getUpdateNameFunction(), ACCOUNT_UPDATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, FinishableFuture<AccountAddress>> nameOwnerFunction =
      getStrategyChain()
          .apply(
              identify(getAccountBaseTemplate().getGetNameOwnerFunction(), ACCOUNT_GETNAMEOWNER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, RawTransaction,
      FinishableFuture<Transaction>> signFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getSignFunction(), ACCOUNT_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, FinishableFuture<Boolean>> verifyFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getVerifyFunction(), ACCOUNT_VERIFY));

  @Override
  public AccountState getState(final Account account) {
    return getState(account.getAddress());
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return getStateFunction().apply(address).get();
  }

  @Override
  public TxHash createName(final Account account, final String name, final long nonce) {
    return getCreateNameFunction().apply(account, name, nonce).get();
  }

  @Override
  public TxHash updateName(final Account ownerAccount, final String name,
      final AccountAddress newOwner, final long nonce) {
    return getUpdateNameFunction().apply(ownerAccount, name, newOwner, nonce).get();
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getNameOwnerFunction().apply(name).get();
  }

  @Override
  public Transaction sign(final Account account, final RawTransaction rawTransaction) {
    return getSignFunction().apply(account, rawTransaction).get();
  }

  @Override
  public boolean verify(final Account account, final Transaction signedTransaction) {
    return getVerifyFunction().apply(account, signedTransaction).get();
  }

}
