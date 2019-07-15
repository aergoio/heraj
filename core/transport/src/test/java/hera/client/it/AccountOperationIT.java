/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.ElectedCandidate;
import hera.api.model.StakeInfo;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void shouldCreateName() {
    // given
    final AergoKey key = createNewKey();

    // when
    final String name = randomName();
    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountAddress owner = aergoClient.getAccountOperation().getNameOwner(name);
    assertEquals(key.getAddress(), owner);
  }

  @Test
  public void shouldCreateNameFailOnInvalidNonce() {
    // given
    final AergoKey key = createNewKey();

    try {
      // when
      final String name = randomName();
      aergoClient.getAccountOperation().createName(key, name, 0L);
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldUpdateName() {
    // given
    final AergoKey originOwner = createNewKey();
    final String name = randomName();
    aergoClient.getAccountOperation().createName(originOwner, name,
        nonceProvider.incrementAndGetNonce(originOwner.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
    aergoClient.getAccountOperation().updateName(originOwner, name, newOwner,
        nonceProvider.incrementAndGetNonce(originOwner.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountAddress owner = aergoClient.getAccountOperation().getNameOwner(name);
    assertEquals(newOwner, owner);
  }

  @Test
  public void shouldUpdateNameFailOnInvalidName() {
    // given
    final AergoKey originOwner = createNewKey();
    final String name = randomName();
    aergoClient.getAccountOperation().createName(originOwner, name,
        nonceProvider.incrementAndGetNonce(originOwner.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      final String invalidName = randomName();
      aergoClient.getAccountOperation().updateName(originOwner, invalidName, newOwner,
          nonceProvider.incrementAndGetNonce(originOwner.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
      e.printStackTrace();
    }
  }

  @Test
  public void shouldUpdateNameFailOnInvalidNonce() {
    // given
    final AergoKey originOwner = createNewKey();
    final String name = randomName();
    aergoClient.getAccountOperation().createName(originOwner, name,
        nonceProvider.incrementAndGetNonce(originOwner.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      aergoClient.getAccountOperation().updateName(originOwner, name, newOwner, 0L);
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldReturnNullOnNameWithoutOwner() {
    // given
    final String name = randomName();

    // when
    final AccountAddress nameOwner = aergoClient.getAccountOperation().getNameOwner(name);

    // then
    assertNull(nameOwner);
  }

  @Test
  public void shouldStake() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();
    final AccountState beforeState = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer stakeAmount = beforeState.getBalance();

    // when
    aergoClient.getAccountOperation().stake(key, stakeAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(key.getAddress());
    assertEquals(key.getAddress(), stakingInfo.getAddress());
    assertEquals(stakeAmount, stakingInfo.getAmount());
    final AccountState afterState = aergoClient.getAccountOperation().getState(key.getAddress());
    assertEquals(Aer.ZERO, afterState.getBalance());
  }

  @Test
  public void shouldStakeFailWithLessThanMiniuumAmount() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();

    try {
      // when
      aergoClient.getAccountOperation().stake(key, Aer.AERGO_ONE.subtract(Aer.ONE),
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
    }
  }

  @Test
  public void shouldStakeFailWithPoor() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer amount = state.getBalance();

    try {
      // when
      aergoClient.getAccountOperation().stake(key, amount.add(Aer.ONE),
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.INSUFFICIENT_BALANCE, e.getCommitStatus());
    }
  }

  @Test
  public void shouldStakeFailWithInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = new AergoKeyGenerator().create();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer amount = state.getBalance();

    try {
      // when
      aergoClient.getAccountOperation().stake(key, amount, 0L);
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldUnstakeFailOnRightAfterStake() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer amount = state.getBalance();
    aergoClient.getAccountOperation().stake(key, amount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstake(key, amount,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
    } catch (Exception e) {
      // then : not enough time has passed to unstake
    }
  }

  @Test
  public void shouldUnstakeFailOnInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer amount = state.getBalance();
    aergoClient.getAccountOperation().stake(key, amount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstake(key, amount, 0L);
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldVoteOnStakedOne() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer amount = state.getBalance();
    aergoClient.getAccountOperation().stake(key, amount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final List<String> candidates = asList(peerIds);
    aergoClient.getAccountOperation().vote(key, "voteBP", candidates,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final List<ElectedCandidate> electeds =
        aergoClient.getAccountOperation().listElected("voteBP", 23);
    assertTrue(peerIds.length <= electeds.size());
    final AccountTotalVote keyVoteTotal =
        aergoClient.getAccountOperation().getVotesOf(key.getAddress());
    assertTrue(1 == keyVoteTotal.getVoteInfos().size());
    assertEquals(peerIds.length, keyVoteTotal.getVoteInfos().get(0).getCandidateIds().size());
  }

  @Test
  public void shouldVoteFailOnUnstakedOne() {
    if (!isDpos()) {
      return;
    }

    // given
    final AergoKey key = createNewKey();

    try {
      // when
      final List<String> candidates = asList(peerIds);
      aergoClient.getAccountOperation().vote(key, "voteBP", candidates,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
    }
  }

  protected boolean isDpos() {
    final String consensus =
        aergoClient.getBlockchainOperation().getBlockchainStatus().getConsensus();
    return consensus.indexOf("dpos") != -1;
  }

}
