/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Peer;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.model.KeyAlias;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StakeVoteIT extends AbstractWalletApiIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("47iTNDNk1S1HgQw4tJkMHBoHQwEkrwd7kbKpZ7ziX5r9jiCcZkeYkTRcuM4ZHbFBovzcgTD2Q", "1234");
  protected WalletApi walletApi;
  protected Authentication authentication;

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
    walletApi = new WalletApiFactory().create(keyStore);
    walletApi.bind(aergoClient);

    final AergoKey key = new AergoKeyGenerator().create();
    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
    authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldStakeAndVoteToCurrentBp() {
    // when
    walletApi.unlock(authentication);
    final Aer minimum = walletApi.queryApi().getChainInfo().getMinimumStakingAmount();
    walletApi.transactionApi().stake(minimum);
    waitForNextBlockToGenerate();

    // then
    final List<Peer> peers = walletApi.queryApi().listPeers(false, true);
    final List<String> candidates = new ArrayList<>();
    final String candidate = peers.get(0).getPeerId();
    candidates.add(candidate);
    walletApi.transactionApi().voteBp(candidates);
    waitForNextBlockToGenerate();

    final AccountTotalVote voteInfo = walletApi.queryApi().getVotesOf(walletApi.getPrincipal());
    assertTrue(voteInfo.getVoteInfos().get(0).getCandidateIds().contains(candidate));
  }

  @Test
  public void shouldStakeFailOnLessThanMinimumAmount() {
    // when
    try {
      walletApi.unlock(authentication);
      final Aer minimum = walletApi.queryApi().getChainInfo().getMinimumStakingAmount();
      walletApi.transactionApi().stake(minimum.subtract(Aer.ONE));
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldStakeFailOnLocked() {
    // when
    try {
      walletApi.transactionApi().stake(Aer.of("10000", Unit.AERGO));
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldVoteFailOnLocked() {
    // when
    walletApi.unlock(authentication);
    final Aer minimum = walletApi.queryApi().getChainInfo().getMinimumStakingAmount();
    walletApi.transactionApi().stake(minimum);
    waitForNextBlockToGenerate();
    final List<Peer> peers = walletApi.queryApi().listPeers(false, true);
    final List<String> candidates = new ArrayList<>();
    final String candidate = peers.get(0).getPeerId();
    candidates.add(candidate);
    walletApi.lock(authentication);

    // then
    try {
      walletApi.transactionApi().voteBp(candidates);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
