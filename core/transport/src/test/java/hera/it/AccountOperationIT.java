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
import hera.api.model.Aer.Unit;
import hera.api.model.Peer;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.VoteInfo;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.RpcCommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final TestClientFactory clientFactory = new TestClientFactory();
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47qD7YfyaZADwyShHWAWegf1ZqYKPT2aSJ9mDFr3qUzBENTLSNZQ7YxC9xqzDdmUTrpEPQUAS", "1234");
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

    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);;
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(rich.getPrincipal())
        .to(key.getAddress())
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(rich.getPrincipal()))
        .build();
    final Transaction signed = rich.sign(rawTransaction);
    logger.debug("Fill tx: ", signed);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldCreateName() {
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
    final String name = randomName();
    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
    aergoClient.getAccountOperation().updateName(key, name, newOwner,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountAddress owner = aergoClient.getAccountOperation().getNameOwner(name);
    assertEquals(newOwner, owner);
  }

  @Test
  public void shouldUpdateNameFailOnInvalidName() {
    // given
    final String name = randomName();
    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      final String invalidName = randomName();
      aergoClient.getAccountOperation().updateName(key, invalidName, newOwner,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (RpcCommitException e) {
      // then
    }
  }

  @Test
  public void shouldUpdateNameFailOnInvalidNonce() {
    // given
    final String name = randomName();
    aergoClient.getAccountOperation().createName(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      aergoClient.getAccountOperation().updateName(key, name, newOwner, 0L);
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
