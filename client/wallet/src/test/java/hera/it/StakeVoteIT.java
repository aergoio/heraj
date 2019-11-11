/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Peer;
import hera.exception.WalletException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class StakeVoteIT extends AbstractWalletApiIT {

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
    } catch (WalletException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldStakeFailOnLocked() {
    // when
    try {
      walletApi.transactionApi().stake(Aer.of("10000", Unit.AERGO));
      fail();
    } catch (WalletException e) {
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
