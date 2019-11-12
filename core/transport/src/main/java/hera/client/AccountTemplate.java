/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.ACCOUNT_CREATE_NAME;
import static hera.client.ClientConstants.ACCOUNT_GETNAMEOWNER;
import static hera.client.ClientConstants.ACCOUNT_GETSTAKINGINFO;
import static hera.client.ClientConstants.ACCOUNT_GETSTATE;
import static hera.client.ClientConstants.ACCOUNT_LIST_ELECTED;
import static hera.client.ClientConstants.ACCOUNT_SIGN;
import static hera.client.ClientConstants.ACCOUNT_STAKING;
import static hera.client.ClientConstants.ACCOUNT_UNSTAKING;
import static hera.client.ClientConstants.ACCOUNT_UPDATE_NAME;
import static hera.client.ClientConstants.ACCOUNT_VERIFY;
import static hera.client.ClientConstants.ACCOUNT_VOTE;
import static hera.client.ClientConstants.ACCOUNT_VOTESOF;

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
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.ElectedCandidate;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.internal.AccountBaseTemplate;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.key.Signer;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountTemplate
    implements AccountOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.accountBaseTemplate.setChannel(channel);;
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.accountBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, Future<AccountState>> stateFunction =
      getStrategyApplier()
          .apply(identify(this.accountBaseTemplate.getStateFunction(), ACCOUNT_GETSTATE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, String, Long,
      Future<TxHash>> createNameFunction =
          getStrategyApplier()
              .apply(identify(this.accountBaseTemplate.getCreateNameFunction(),
                  ACCOUNT_CREATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, String, AccountAddress, Long,
      Future<TxHash>> updateNameFunction =
          getStrategyApplier().apply(
              identify(this.accountBaseTemplate.getUpdateNameFunction(), ACCOUNT_UPDATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<String, Long, Future<AccountAddress>> nameOwnerFunction =
      getStrategyApplier().apply(
          identify(this.accountBaseTemplate.getGetNameOwnerFunction(), ACCOUNT_GETNAMEOWNER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, Aer, Long,
      Future<TxHash>> stakingFunction =
          getStrategyApplier().apply(
              identify(this.accountBaseTemplate.getStakingFunction(), ACCOUNT_STAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, Aer, Long,
      Future<TxHash>> unstakingFunction =
          getStrategyApplier().apply(
              identify(this.accountBaseTemplate.getUnstakingFunction(), ACCOUNT_UNSTAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, Future<StakeInfo>> stakingInfoFunction =
      getStrategyApplier().apply(
          identify(this.accountBaseTemplate.getStakingInfoFunction(), ACCOUNT_GETSTAKINGINFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, RawTransaction,
      Future<Transaction>> signFunction =
          getStrategyApplier()
              .apply(identify(this.accountBaseTemplate.getSignFunction(), ACCOUNT_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, Future<Boolean>> verifyFunction =
      getStrategyApplier()
          .apply(identify(this.accountBaseTemplate.getVerifyFunction(), ACCOUNT_VERIFY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, String, List<String>, Long,
      Future<TxHash>> voteFunction =
          getStrategyApplier().apply(
              identify(this.accountBaseTemplate.getVoteFunction(), ACCOUNT_VOTE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<String, Integer,
      Future<List<ElectedCandidate>>> listElectedFunction = getStrategyApplier().apply(
          identify(this.accountBaseTemplate.getListElectedFunction(), ACCOUNT_LIST_ELECTED));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, Future<AccountTotalVote>> votesOfFunction =
      getStrategyApplier().apply(
          identify(this.accountBaseTemplate.getVotesOfFunction(), ACCOUNT_VOTESOF));

  @Override
  public AccountState getState(final Account account) {
    return getState(account.getAddress());
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    try {
      return getStateFunction().apply(address).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash createName(final Account account, final String name, final long nonce) {
    if (!(account.getKey() instanceof Signer)) {
      throw new UnsupportedOperationException();
    }
    return createName(account.getKey(), name, nonce);
  }

  @Override
  public TxHash createName(final Signer signer, final String name, final long nonce) {
    try {
      return getCreateNameFunction().apply(signer, name, nonce).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash updateName(final Account account, final String name, final AccountAddress newOwner,
      final long nonce) {
    if (!(account.getKey() instanceof Signer)) {
      throw new UnsupportedOperationException();
    }
    return updateName(account.getKey(), name, newOwner, nonce);
  }

  @Override
  public TxHash updateName(final Signer signer, final String name, final AccountAddress newOwner,
      final long nonce) {
    try {
      return getUpdateNameFunction().apply(signer, name, newOwner, nonce).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getNameOwner(name, 0);
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    try {
      return getNameOwnerFunction().apply(name, blockNumber).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash stake(final Account account, final Aer amount, final long nonce) {
    if (!(account.getKey() instanceof Signer)) {
      throw new UnsupportedOperationException();
    }
    return stake(account.getKey(), amount, nonce);
  }

  @Override
  public TxHash stake(final Signer signer, final Aer amount, final long nonce) {
    try {
      return getStakingFunction().apply(signer, amount, nonce).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash unstake(final Account account, final Aer amount, final long nonce) {
    if (!(account.getKey() instanceof Signer)) {
      throw new UnsupportedOperationException();
    }
    return unstake(account.getKey(), amount, nonce);
  }

  @Override
  public TxHash unstake(final Signer signer, final Aer amount, final long nonce) {
    try {
      return getUnstakingFunction().apply(signer, amount, nonce).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    try {
      return getStakingInfoFunction().apply(accountAddress).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Transaction sign(final Account account, final RawTransaction rawTransaction) {
    try {
      return getSignFunction().apply(account, rawTransaction).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean verify(final Account account, final Transaction signedTransaction) {
    try {
      return getVerifyFunction().apply(account, signedTransaction).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash vote(final Signer signer, final String voteId, final List<String> candidates,
      final long nonce) {
    try {
      return getVoteFunction().apply(signer, voteId, candidates, nonce).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    try {
      return getListElectedFunction().apply(voteId, showCount).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    try {
      return getVotesOfFunction().apply(accountAddress).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
