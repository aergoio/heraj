/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.api.model.PeerMetric;
import hera.api.model.VotingInfo;
import hera.exception.CommitException;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void testBlockchainStatus() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    logger.info("Current best block hash: {}", status.getBestBlockHash());
    logger.info("Current best block height: {}", status.getBestHeight());

    assertNotNull(status.getBestBlockHash());
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testNodeStatus() {
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();
    logger.info("Current node status: {}", nodeStatus);

    assertNotNull(nodeStatus);
  }

  @Test
  public void testListPeers() {
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers();
    logger.info("Current node peers: {}", peers);

    assertNotNull(peers);
  }

  @Test
  public void testListPeerMetrics() {
    final List<PeerMetric> peerMetrics = aergoClient.getBlockchainOperation().listPeerMetrics();
    logger.info("Current node peer metrics: {}", peerMetrics);

    assertNotNull(peerMetrics);
  }

  @Test
  public void testVoting() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);

      aergoClient.getAccountOperation().stake(account, Aer.of("1", Unit.AERGO),
          account.incrementAndGetNonce());
      waitForNextBlockToGenerate();

      final PeerId peerId = new PeerId("16Uiu2HAmV6iVGuN31sZTz2GDicFPpBr6eaHn1mVM499BGwSBf6Nb");
      aergoClient.getBlockchainOperation().vote(account, peerId, account.incrementAndGetNonce());
      waitForNextBlockToGenerate();

      lockAccount(account, password);

      final List<BlockProducer> electedBlockProducers =
          aergoClient.getBlockchainOperation().listElectedBlockProducers(23);
      logger.info("Current elected bps: {}", electedBlockProducers);
      assertTrue(1 <= electedBlockProducers.size());
      assertEquals(peerId, electedBlockProducers.get(0).getPeerId());

      final List<VotingInfo> votingInfos =
          aergoClient.getBlockchainOperation().listVotesOf(account.getAddress());
      assertTrue(1 == votingInfos.size());
      assertEquals(peerId, votingInfos.get(0).getPeerId());
    }
  }

  @Test
  public void testVotingOnUnStakedOne() {
    for (final Account account : supplyAccounts()) {
      unlockAccount(account, password);

      try {
        final PeerId peerId = new PeerId("16Uiu2HAmV6iVGuN31sZTz2GDicFPpBr6eaHn1mVM499BGwSBf6Nb");
        aergoClient.getBlockchainOperation().vote(account, peerId, account.incrementAndGetNonce());
        fail();
      } catch (CommitException e) {
        // good we expected this
      }

      lockAccount(account, password);
    }
  }

}
