/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.net.InetAddress;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Peer {

  @Getter
  protected InetAddress address;

  @Getter
  protected int port;

  @Getter
  protected String peerId;

  @Getter
  protected long bestHeight;

  @Getter
  protected BlockHash bestBlockHash;

  @Getter
  protected int state;

  @Getter
  protected boolean hidden;

  @Getter
  protected long lashCheck;

  @Getter
  protected boolean selfPeer;

  /**
   * Peer constructor.
   *
   * @param address a peer address
   * @param port a peer port
   * @param peerId a peer id
   * @param bestHeight a best height of peer
   * @param bestBlockHash a best block hash of peer
   * @param state a peer state
   * @param hidden whether a peer is hidden or not
   * @param lashCheck a lash check value
   * @param selfPeer whether it is a peer which have received request or not
   */
  @ApiAudience.Private
  public Peer(final InetAddress address, final int port, final String peerId,
      final long bestHeight, final BlockHash bestBlockHash,
      final int state, final boolean hidden,
      final long lashCheck, final boolean selfPeer) {
    assertNotNull(address, "Peer address must not null");
    assertNotNull(peerId, "Peer id must not null");
    assertTrue(bestHeight >= 0L, "Peer best block height >= 0");
    assertNotNull(bestBlockHash, "Peer best block hash must not null");
    this.address = address;
    this.port = port;
    this.peerId = peerId;
    this.bestHeight = bestHeight;
    this.bestBlockHash = bestBlockHash;
    this.state = state;
    this.hidden = hidden;
    this.lashCheck = lashCheck;
    this.selfPeer = selfPeer;
  }

}
