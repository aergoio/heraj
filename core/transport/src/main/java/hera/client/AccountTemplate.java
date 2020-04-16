/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.api.AccountOperation;
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
import hera.key.Signer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

class AccountTemplate extends AbstractTemplate implements AccountOperation {

  protected final AccountMethods accountMethods = new AccountMethods();

  AccountTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public AccountState getState(Account account) {
    return getState(account.getAddress());
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return request(new Callable<AccountState>() {
      @Override
      public AccountState call() throws Exception {
        return requester.request(accountMethods
            .getAccountState()
            .toInvocation(Arrays.<Object>asList(address)));
      }
    });
  }

  @Override
  public TxHash createName(final Account account, final String name, final long nonce) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TxHash createName(final Signer signer, final String name, final long nonce) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(accountMethods
            .getCreateName()
            .toInvocation(Arrays.asList(signer, name, nonce)));
      }
    });
  }

  @Override
  public TxHash updateName(final Account owner, final String name, final AccountAddress newOwner,
      final long nonce) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public TxHash updateName(final Signer signer, final String name, final AccountAddress newOwner,
      final long nonce) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(accountMethods
            .getUpdateName()
            .toInvocation(Arrays.asList(signer, name, newOwner, nonce)));
      }
    });
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getNameOwner(name, 0);
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    return request(new Callable<AccountAddress>() {
      @Override
      public AccountAddress call() throws Exception {
        return requester.request(accountMethods
            .getNameOwner()
            .toInvocation(Arrays.<Object>asList(name, blockNumber)));
      }
    });
  }

  @Override
  public TxHash stake(final Account account, final Aer amount, final long nonce) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public TxHash stake(final Signer signer, final Aer amount, final long nonce) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(accountMethods
            .getStake()
            .toInvocation(Arrays.asList(signer, amount, nonce)));
      }
    });
  }

  @Override
  public TxHash unstake(Account account, Aer amount, long nonce) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public TxHash unstake(final Signer signer, final Aer amount, final long nonce) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(accountMethods
            .getUnstake()
            .toInvocation(Arrays.asList(signer, amount, nonce)));
      }
    });
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    return request(new Callable<StakeInfo>() {
      @Override
      public StakeInfo call() throws Exception {
        return requester.request(accountMethods
            .getStakeInfo()
            .toInvocation(Arrays.<Object>asList(accountAddress)));
      }
    });
  }

  @Override
  public TxHash vote(final Signer signer, final String voteId, final List<String> candidates,
      final long nonce) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(accountMethods
            .getVote()
            .toInvocation(Arrays.asList(signer, voteId, candidates, nonce)));
      }
    });
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    return request(new Callable<AccountTotalVote>() {
      @Override
      public AccountTotalVote call() throws Exception {
        return requester.request(accountMethods
            .getVoteOf()
            .toInvocation(Arrays.<Object>asList(accountAddress)));
      }
    });
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    return request(new Callable<List<ElectedCandidate>>() {
      @Override
      public List<ElectedCandidate> call() throws Exception {
        return requester.request(accountMethods
            .getListElected()
            .toInvocation(Arrays.<Object>asList(voteId, showCount)));
      }
    });
  }

  @Override
  public Transaction sign(final Account account, final RawTransaction rawTransaction) {
    throw new UnsupportedOperationException("Use AergoKey instead");
  }

  @Override
  public boolean verify(final Account account, final Transaction transaction) {
    throw new UnsupportedOperationException("Use AergoSignVerifier instead");
  }

}
