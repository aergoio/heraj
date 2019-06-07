/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER;
import static hera.TransportConstants.ACCOUNT_GETSTAKINGINFO;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_LIST_ELECTED;
import static hera.TransportConstants.ACCOUNT_SIGN;
import static hera.TransportConstants.ACCOUNT_STAKING;
import static hera.TransportConstants.ACCOUNT_UNSTAKING;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME;
import static hera.TransportConstants.ACCOUNT_VERIFY;
import static hera.TransportConstants.ACCOUNT_VOTE;
import static hera.TransportConstants.ACCOUNT_VOTESOF;
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
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.ElectedCandidate;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.internal.AccountBaseTemplate;
import hera.client.internal.FinishableFuture;
import hera.key.Signer;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
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
      FinishableFuture<TxHash>> deprecatedCreateNameFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getDeprecatedCreateNameFunction(),
                  ACCOUNT_CREATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, String, Long,
      FinishableFuture<TxHash>> createNameFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getCreateNameFunction(),
                  ACCOUNT_CREATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Account, String, AccountAddress, Long,
      FinishableFuture<TxHash>> deprecatedUpdateNameFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getDeprecatedUpdateNameFunction(),
                  ACCOUNT_UPDATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, String, AccountAddress, Long,
      FinishableFuture<TxHash>> updateNameFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getUpdateNameFunction(), ACCOUNT_UPDATE_NAME));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<String, FinishableFuture<AccountAddress>> nameOwnerFunction =
      getStrategyChain()
          .apply(
              identify(getAccountBaseTemplate().getGetNameOwnerFunction(), ACCOUNT_GETNAMEOWNER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Account, Aer, Long,
      FinishableFuture<TxHash>> deprecatedStakingFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getDeprecatedStakingFunction(), ACCOUNT_STAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, Aer, Long,
      FinishableFuture<TxHash>> stakingFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getStakingFunction(), ACCOUNT_STAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Account, Aer, Long,
      FinishableFuture<TxHash>> deprecatedUnstakingFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getDeprecatedUnstakingFunction(),
                  ACCOUNT_UNSTAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<Signer, Aer, Long,
      FinishableFuture<TxHash>> unstakingFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getUnstakingFunction(), ACCOUNT_UNSTAKING));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, FinishableFuture<StakeInfo>> stakingInfoFunction =
      getStrategyChain().apply(
          identify(getAccountBaseTemplate().getStakingInfoFunction(), ACCOUNT_GETSTAKINGINFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, RawTransaction,
      FinishableFuture<Transaction>> signFunction =
          getStrategyChain()
              .apply(identify(getAccountBaseTemplate().getSignFunction(), ACCOUNT_SIGN));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Account, Transaction, FinishableFuture<Boolean>> verifyFunction =
      getStrategyChain()
          .apply(identify(getAccountBaseTemplate().getVerifyFunction(), ACCOUNT_VERIFY));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function4<Signer, String, List<String>, Long,
      FinishableFuture<TxHash>> voteFunction =
          getStrategyChain().apply(
              identify(getAccountBaseTemplate().getVoteFunction(), ACCOUNT_VOTE));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<String, Integer,
      FinishableFuture<List<ElectedCandidate>>> listElectedFunction = getStrategyChain().apply(
          identify(getAccountBaseTemplate().getListElectedFunction(), ACCOUNT_LIST_ELECTED));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<AccountAddress, FinishableFuture<AccountTotalVote>> votesOfFunction =
      getStrategyChain().apply(
          identify(getAccountBaseTemplate().getVotesOfFunction(), ACCOUNT_VOTESOF));

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
    return getDeprecatedCreateNameFunction().apply(account, name, nonce).get();
  }

  @Override
  public TxHash createName(final Signer signer, final String name, final long nonce) {
    return getCreateNameFunction().apply(signer, name, nonce).get();
  }

  @Override
  public TxHash updateName(final Account ownerAccount, final String name,
      final AccountAddress newOwner, final long nonce) {
    return getDeprecatedUpdateNameFunction().apply(ownerAccount, name, newOwner, nonce).get();
  }

  @Override
  public TxHash updateName(final Signer signer, final String name, final AccountAddress newOwner,
      final long nonce) {
    return getUpdateNameFunction().apply(signer, name, newOwner, nonce).get();
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getNameOwnerFunction().apply(name).get();
  }

  @Override
  public TxHash stake(final Account account, final Aer amount, final long nonce) {
    return getDeprecatedStakingFunction().apply(account, amount, nonce).get();
  }

  @Override
  public TxHash stake(final Signer signer, final Aer amount, final long nonce) {
    return getStakingFunction().apply(signer, amount, nonce).get();
  }

  @Override
  public TxHash unstake(final Account account, final Aer amount, final long nonce) {
    return getDeprecatedUnstakingFunction().apply(account, amount, nonce).get();
  }

  @Override
  public TxHash unstake(final Signer signer, final Aer amount, final long nonce) {
    return getUnstakingFunction().apply(signer, amount, nonce).get();
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    return getStakingInfoFunction().apply(accountAddress).get();
  }

  @Override
  public Transaction sign(final Account account, final RawTransaction rawTransaction) {
    return getSignFunction().apply(account, rawTransaction).get();
  }

  @Override
  public boolean verify(final Account account, final Transaction signedTransaction) {
    return getVerifyFunction().apply(account, signedTransaction).get();
  }

  @Override
  public TxHash vote(final Signer signer, final String voteId, final List<String> candidates,
      final long nonce) {
    return getVoteFunction().apply(signer, voteId, candidates, nonce).get();
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    return getListElectedFunction().apply(voteId, showCount).get();
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    return getVotesOfFunction().apply(accountAddress).get();
  }

}
