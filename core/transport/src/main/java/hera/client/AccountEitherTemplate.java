/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME_EITHER;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER_EITHER;
import static hera.TransportConstants.ACCOUNT_GETSTATE_EITHER;
import static hera.TransportConstants.ACCOUNT_SIGN_EITHER;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME_EITHER;
import static hera.TransportConstants.ACCOUNT_VERIFY_EITHER;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountEitherOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountEitherTemplate
    implements AccountEitherOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getAccountBaseTemplate().setChannel(channel);;
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, ResultOrErrorFuture<AccountState>> stateFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getStateFunction(), ACCOUNT_GETSTATE_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Account, String, Long,
      ResultOrErrorFuture<TxHash>> createNameFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getCreateNameFunction(),
                  ACCOUNT_CREATE_NAME_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, String, AccountAddress, Long,
      ResultOrErrorFuture<TxHash>> updateNameFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getUpdateNameFunction(),
                  ACCOUNT_UPDATE_NAME_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, ResultOrErrorFuture<AccountAddress>> nameOwnerFunction =
      getStrategyChain()
          .apply(
              identify(getAccountBaseTemplate().getGetNameOwnerFunction(),
                  ACCOUNT_GETNAMEOWNER_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, RawTransaction, ResultOrErrorFuture<Transaction>> signFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getSignFunction(), ACCOUNT_SIGN_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, ResultOrErrorFuture<Boolean>> verifyFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getVerifyFunction(), ACCOUNT_VERIFY_EITHER));

  @Override
  public ResultOrError<AccountState> getState(final Account account) {
    return getState(account.getAddress());
  }

  @Override
  public ResultOrError<AccountState> getState(final AccountAddress address) {
    return getStateFunction().apply(address).get();
  }

  @Override
  public ResultOrError<TxHash> createName(final Account account, final String name,
      final long nonce) {
    return getCreateNameFunction().apply(account, name, nonce).get();
  }

  @Override
  public ResultOrError<TxHash> updateName(final Account ownerAccount, final String name,
      final AccountAddress newOwner, final long nonce) {
    return getUpdateNameFunction().apply(ownerAccount, name, newOwner, nonce).get();
  }

  @Override
  public ResultOrError<AccountAddress> getNameOwner(final String name) {
    return getNameOwnerFunction().apply(name).get();
  }

  @Override
  public ResultOrError<Transaction> sign(final Account account, final RawTransaction transaction) {
    return getSignFunction().apply(account, transaction).get();
  }

  @Override
  public ResultOrError<Boolean> verify(final Account account, final Transaction transaction) {
    return getVerifyFunction().apply(account, transaction).get();
  }
}
