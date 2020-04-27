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
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoSignVerifier;
import hera.key.Signer;
import java.util.Arrays;
import java.util.List;

class AccountTemplate extends AbstractTemplate implements AccountOperation {

  protected final AccountMethods accountMethods = new AccountMethods();

  AccountTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public AccountState getState(final Account account) {
    return getState(account.getAddress());
  }

  @Override
  public AccountState getState(final AccountAddress address) {
    return request(accountMethods.getAccountState(), Arrays.<Object>asList(address));
  }

  @Override
  public TxHash createName(final Account account, final String name, final long nonce) {
    return createNameTx(account.getKey(), Name.of(name), nonce);
  }

  @Override
  public TxHash createName(final Signer signer, final String name, final long nonce) {
    return createNameTx(signer, Name.of(name), nonce);
  }

  @Override
  public TxHash createNameTx(final Signer signer, final Name name, final long nonce) {
    return request(accountMethods.getCreateNameTx(), Arrays.asList(signer, name, nonce));
  }

  @Override
  public TxHash updateName(final Account owner, final String name, final AccountAddress newOwner,
      final long nonce) {
    return updateNameTx(owner.getKey(), Name.of(name), newOwner, nonce);
  }

  @Override
  public TxHash updateName(final Signer signer, final String name, final AccountAddress newOwner,
      final long nonce) {
    return updateNameTx(signer, Name.of(name), newOwner, nonce);
  }

  @Override
  public TxHash updateNameTx(final Signer signer, final Name name, final AccountAddress newOwner,
      final long nonce) {
    return request(accountMethods.getUpdateNameTx(), Arrays.asList(signer, name, newOwner, nonce));
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getNameOwner(Name.of(name));
  }

  @Override
  public AccountAddress getNameOwner(final Name name) {
    return getNameOwner(name, 0L);
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    return getNameOwner(Name.of(name), blockNumber);
  }

  @Override
  public AccountAddress getNameOwner(final Name name, long blockNumber) {
    return request(accountMethods.getNameOwner(), Arrays.<Object>asList(name, blockNumber));
  }

  @Override
  public TxHash stake(final Account account, final Aer amount, final long nonce) {
    return stakeTx(account.getKey(), amount, nonce);
  }

  @Override
  public TxHash stake(final Signer signer, final Aer amount, final long nonce) {
    return stakeTx(signer, amount, nonce);
  }

  @Override
  public TxHash stakeTx(final Signer signer, final Aer amount, final long nonce) {
    return request(accountMethods.getStakeTx(), Arrays.asList(signer, amount, nonce));
  }

  @Override
  public TxHash unstake(final Account account, final Aer amount, final long nonce) {
    return unstakeTx(account.getKey(), amount, nonce);
  }

  @Override
  public TxHash unstake(final Signer signer, final Aer amount, final long nonce) {
    return unstakeTx(signer, amount, nonce);
  }

  @Override
  public TxHash unstakeTx(final Signer signer, final Aer amount, final long nonce) {
    return request(accountMethods.getUnstakeTx(), Arrays.asList(signer, amount, nonce));
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    return request(accountMethods.getStakeInfo(), Arrays.<Object>asList(accountAddress));
  }

  @Override
  public TxHash vote(final Signer signer, final String voteId, final List<String> candidates,
      final long nonce) {
    return voteTx(signer, voteId, candidates, nonce);
  }

  @Override
  public TxHash voteTx(final Signer signer, final String voteId, final List<String> candidates,
      final long nonce) {
    return request(accountMethods.getVoteTx(), Arrays.asList(signer, voteId, candidates, nonce));
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    return request(accountMethods.getVoteOf(), Arrays.<Object>asList(accountAddress));
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    return request(accountMethods.getListElected(), Arrays.<Object>asList(voteId, showCount));
  }

  @Override
  public Transaction sign(final Account account, final RawTransaction rawTransaction) {
    return account.getKey().sign(rawTransaction);
  }

  @Override
  public boolean verify(final Account account, final Transaction transaction) {
    return new AergoSignVerifier().verify(transaction);
  }

}
