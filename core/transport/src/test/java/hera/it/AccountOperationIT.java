/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Peer;
import hera.api.model.StakeInfo;
import hera.api.model.VoteInfo;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void shouldCreateName() {
    // when
    final AergoKey key = createNewKey();
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
    try {
      // when
      final AergoKey key = createNewKey();
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
    // when
    final String name = randomName();
    final AccountAddress nameOwner = aergoClient.getAccountOperation().getNameOwner(name);

    // then
    assertNull(nameOwner);
  }

  @Test
  public void shouldStake() {
    // when
    final AergoKey key = createNewKey();
    final AccountState beforeState = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stake(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(key.getAddress());
    assertEquals(key.getAddress(), stakingInfo.getAddress());
    assertEquals(minimumAmount, stakingInfo.getAmount());
    final AccountState afterState = aergoClient.getAccountOperation().getState(key.getAddress());
    assertEquals(beforeState.getBalance(), afterState.getBalance().add(minimumAmount));
  }

  @Test
  public void shouldStakeFailWithLessThanMinimumAmount() {
    try {
      // when
      final AergoKey key = createNewKey();
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stake(key, minimumAmount.subtract(Aer.ONE),
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
    }
  }

  @Test
  public void shouldStakeFailWithPoor() {
    try {
      // when
      final AergoKey key = new AergoKeyGenerator().create();
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stake(key, minimumAmount,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.INSUFFICIENT_BALANCE, e.getCommitStatus());
    }
  }

  @Test
  public void shouldStakeFailWithInvalidNonce() {
    try {
      // when
      final AergoKey key = createNewKey();
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stake(key, minimumAmount, 0L);
      fail();
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldUnstakeFailOnRightAfterStake() {
    // given
    final AergoKey key = createNewKey();
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stake(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstake(key, minimumAmount,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
    } catch (Exception e) {
      // then : not enough time has passed to unstake
    }
  }

  @Test
  public void shouldUnstakeFailOnInvalidNonce() {
    // given
    final AergoKey key = createNewKey();
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stake(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstake(key, minimumAmount, 0L);
    } catch (RpcCommitException e) {
      // then
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldVoteOnStakedOne() {
    // given
    final AergoKey key = createNewKey();
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stake(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers(true, true);
    final List<String> expected = new ArrayList<>();
    expected.add(peers.get(0).getPeerId());
    aergoClient.getAccountOperation().vote(key, "voteBP", expected,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountTotalVote keyVoteTotal =
        aergoClient.getAccountOperation().getVotesOf(key.getAddress());
    final List<VoteInfo> fuck = keyVoteTotal.getVoteInfos();
    System.out.println(fuck);
    final List<String> actual = fuck.get(0).getCandidateIds();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldVoteFailOnUnstakedOne() {
    try {
      // when
      final AergoKey key = createNewKey();
      final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers(true, true);
      final List<String> candidates = new ArrayList<>();
      candidates.add(peers.get(0).getPeerId());
      aergoClient.getAccountOperation().vote(key, "voteBP", candidates,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
    }
  }

}
