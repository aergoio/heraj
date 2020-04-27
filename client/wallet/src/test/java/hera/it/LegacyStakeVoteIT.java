/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ElectedCandidate;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LegacyStakeVoteIT extends AbstractLegacyWalletIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("47u3AHf1buyuJrJ1yijeCHPAxxAy1L4znKW7KzAJi4H3vpe4ArTPh9fJf19hcwKMPjctEYhcq", "1234");
  protected AergoKey key;

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
    key = new AergoKeyGenerator().create();

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    ;
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldStakeOnUnlockedOne() throws IOException {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getAccountState();
    final Aer stakingAmount = preState.getBalance();

    // when
    wallet.stake(stakingAmount);
    waitForNextBlockToGenerate();

    // then
    final StakeInfo stakingInfo = wallet.getStakingInfo();
    assertEquals(stakingAmount, stakingInfo.getAmount());
    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);
  }

  @Test
  public void shouldStakeThrowErrorOnLockedOne() throws IOException {
    try {
      // when
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      final Aer stakingAmount = Aer.of("10", Unit.AERGO);
      wallet.stake(stakingAmount);
    } catch (Throwable e) {
      // then
    }
  }

  @Test
  public void shouldVoteOnStakedOne() {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    final AccountState preState = wallet.getAccountState();
    final Aer stakingAmount = preState.getBalance();
    wallet.stake(stakingAmount);
    waitForNextBlockToGenerate();

    // when
    final List<ElectedCandidate> peers = wallet.listElectedBps(10);
    final List<String> peerIds = new ArrayList<>(peers.size());
    for (final ElectedCandidate peer : peers) {
      peerIds.add(peer.getCandidateId());
    }
    wallet.voteBp(peerIds);
    waitForNextBlockToGenerate();

    // then
    final List<ElectedCandidate> electedBlockProducers = wallet.listElectedBps(100);
    final AccountTotalVote accountTotalVote = wallet.getVotes();
    assertTrue(1 <= accountTotalVote.getVoteInfos().size());
    assertEquals(peerIds.size(), accountTotalVote.getVoteInfos().get(0).getCandidateIds().size());
  }

  @Test
  public void shouldVoteFailOnUnStakedOn() {
    try {
      // given
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      // when
      final List<ElectedCandidate> peers = wallet.listElectedBps(10);
      final List<String> peerIds = new ArrayList<>(peers.size());
      for (final ElectedCandidate peer : peers) {
        peerIds.add(peer.getCandidateId());
      }
      wallet.voteBp(peerIds);
      fail();
    } catch (HerajException e) {
      // then
    }
  }

}
