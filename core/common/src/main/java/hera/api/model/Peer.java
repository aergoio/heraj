/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

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
  protected PeerId peerId;

  @Getter
  protected BlockchainStatus blockchainStatus;

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
   * @param blockchainStatus a blockchain status of peer
   * @param state a peer state
   * @param hidden whether a peer is hidden or not
   * @param lashCheck a lash check value
   * @param selfPeer whether it is a peer which have received request or not
   */
  @ApiAudience.Private
  public Peer(final InetAddress address, final int port, final PeerId peerId,
      final BlockchainStatus blockchainStatus,
      final int state, final boolean hidden, final long lashCheck, final boolean selfPeer) {
    assertNotNull(address, "Peer address must not null");
    assertNotNull(peerId, "Peer id must not null");
    assertNotNull(blockchainStatus, "Peer blockchain status must not null");
    this.address = address;
    this.port = port;
    this.peerId = peerId;
    this.blockchainStatus = blockchainStatus;
    this.state = state;
    this.hidden = hidden;
    this.lashCheck = lashCheck;
    this.selfPeer = selfPeer;
  }

}
