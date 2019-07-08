/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ElectedCandidate;
import hera.api.model.StakeInfo;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateAndGetName() {
    final AergoKey key = createNewKey();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    assertEquals(key.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();

    aergoClient.getAccountOperation().updateName(key, name, newOwner,
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    long bestHeight = aergoClient.getBlockchainOperation().getBlockchainStatus().getBestHeight();

    waitForNextBlockToGenerate();
    
    assertEquals(newOwner, aergoClient.getAccountOperation().getNameOwner(name, bestHeight + 1));
    assertEquals(newOwner, aergoClient.getAccountOperation().getNameOwner(name));
  }

  @Test
  public void testGetNameOwnerOfInvalidName() {
    assertNull(aergoClient.getAccountOperation().getNameOwner(randomUUID().toString()));
  }

  @Test
  public void testCreateWithInvalidNonce() {
    final AergoKey key = createNewKey();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    try {
      aergoClient.getAccountOperation().createName(key, name, 0L);
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testUpdateWithInvalidNonce() {
    final AergoKey key = createNewKey();
    final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    assertEquals(key.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();

    try {
      aergoClient.getAccountOperation().updateName(key, name, newOwner, 0L);
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testStakingAndUnstaking() {
    if (!isDpos()) {
      return;
    }

    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    aergoClient.getAccountOperation().stake(key, state.getBalance(),
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    final StakeInfo stakingInfoAfterStaked =
        aergoClient.getAccountOperation().getStakingInfo(key.getAddress());
    assertEquals(key.getAddress(), stakingInfoAfterStaked.getAddress());
    assertEquals(state.getBalance(), stakingInfoAfterStaked.getAmount());

    try {
      aergoClient.getAccountOperation().unstake(key, stakingInfoAfterStaked.getAmount(),
          nonceProvider.incrementAndGetNonce(key.getAddress()));
    } catch (Exception e) {
      // good we expected this not enough time has passed to unstake
    }
  }

  @Test
  public void testStakingWithInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());
    try {
      aergoClient.getAccountOperation().stake(key, state.getBalance(), 0L);
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testUnstakingWithInvalidNonce() {
    if (!isDpos()) {
      return;
    }

    final AergoKey key = createNewKey();
    final AccountState state = aergoClient.getAccountOperation().getState(key.getAddress());

    aergoClient.getAccountOperation().stake(key, state.getBalance(),
        nonceProvider.incrementAndGetNonce(key.getAddress()));

    waitForNextBlockToGenerate();

    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(key.getAddress());
    assertEquals(key.getAddress(), stakingInfo.getAddress());
    assertEquals(state.getBalance(), stakingInfo.getAmount());

    try {
      aergoClient.getAccountOperation().unstake(key, stakingInfo.getAmount(), 0L);
      fail();
    } catch (RpcCommitException e) {
      assertEquals(RpcCommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void testVote() {
    if (!isDpos()) {
      return;
    }

    final AergoKey key = createNewKey();

    aergoClient.getAccountOperation().stake(key, Aer.of("10000", Unit.AERGO),
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(key.getAddress());
    logger.info("Staking info: {}", stakingInfo);

    final List<String> candidates = asList(peerIds);
    aergoClient.getAccountOperation().vote(key, "voteBP", candidates,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    final List<ElectedCandidate> electedBlockProducers =
        aergoClient.getBlockchainOperation().listElected("voteBP", 23);
    logger.info("Current elected bps: {}", electedBlockProducers);
    assertTrue(peerIds.length <= electedBlockProducers.size());

    final AccountTotalVote keyVoteTotal =
        aergoClient.getBlockchainOperation().getVotesOf(key.getAddress());
    assertTrue(1 == keyVoteTotal.getVoteInfos().size());
    assertEquals(peerIds.length, keyVoteTotal.getVoteInfos().get(0).getCandidateIds().size());
  }

  @Test
  public void testVoteOnUnStakedOne() {
    if (!isDpos()) {
      return;
    }

    final AergoKey key = createNewKey();

    try {
      final List<String> candidates = asList(peerIds);
      aergoClient.getAccountOperation().vote(key, "voteBP", candidates,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

}
