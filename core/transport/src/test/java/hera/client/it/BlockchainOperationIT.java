/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.exception.RpcCommitException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void testBlockchainStatus() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    logger.info("Current blockchain status: {}", status);

    assertNotNull(status.getBestBlockHash());
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testChainInfo() {
    final ChainInfo chainInfo = aergoClient.getBlockchainOperation().getChainInfo();
    logger.info("Current node chain info: {}", chainInfo);

    assertNotNull(chainInfo);
  }

  @Test
  public void testServerInfo() {
    final ServerInfo serverInfo =
        aergoClient.getBlockchainOperation().getServerInfo(new ArrayList<String>());
    logger.info("Current server info: {}", serverInfo);

    assertNotNull(serverInfo);
  }

  @Test
  public void testNodeStatus() {
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();
    logger.info("Current node status: {}", nodeStatus);

    assertNotNull(nodeStatus);
  }

  @Test
  public void testListPeers() {
    final List<Peer> peersWithoutArgs = aergoClient.getBlockchainOperation().listPeers();
    logger.info("Current node peers: {}", peersWithoutArgs);
    assertNotNull(peersWithoutArgs);

    final List<Peer> peersWithArgs = aergoClient.getBlockchainOperation().listPeers(true, true);
    logger.info("Current node peers: {}", peersWithArgs);
    assertNotNull(peersWithArgs);
    assertTrue(0 < peersWithArgs.size());
  }

  @Test
  public void testListPeerMetrics() {
    final List<PeerMetric> peerMetrics = aergoClient.getBlockchainOperation().listPeerMetrics();
    logger.info("Current node peer metrics: {}", peerMetrics);

    assertNotNull(peerMetrics);
  }

  @Test
  public void testVote() {
    if (!isDpos()) {
      return;
    }

    final Account account = supplyLocalAccount();

    aergoClient.getAccountOperation().stake(account, Aer.of("10000", Unit.AERGO),
        account.incrementAndGetNonce());
    waitForNextBlockToGenerate();

    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakingInfo(account.getAddress());
    logger.info("Staking info: {}", stakingInfo);

    final List<String> candidates = asList(peerIds);
    aergoClient.getBlockchainOperation().vote(account, "voteBP", candidates,
        account.incrementAndGetNonce());
    waitForNextBlockToGenerate();

    final List<ElectedCandidate> electedBlockProducers =
        aergoClient.getBlockchainOperation().listElected("voteBP", 23);
    logger.info("Current elected bps: {}", electedBlockProducers);
    assertTrue(peerIds.length <= electedBlockProducers.size());

    final AccountTotalVote accountVoteTotal =
        aergoClient.getBlockchainOperation().getVotesOf(account.getAddress());
    assertTrue(1 == accountVoteTotal.getVoteInfos().size());
    assertEquals(peerIds.length, accountVoteTotal.getVoteInfos().get(0).getCandidateIds().size());
  }

  @Test
  public void testVoteOnUnStakedOne() {
    if (!isDpos()) {
      return;
    }

    final Account account = supplyLocalAccount();

    try {
      final List<String> candidates = asList(peerIds);
      aergoClient.getBlockchainOperation().vote(account, "voteBP", candidates,
          account.incrementAndGetNonce());
      fail();
    } catch (RpcCommitException e) {
      // good we expected this
    }
  }

}
