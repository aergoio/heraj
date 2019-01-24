/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class BlockProducer {

  @Getter
  protected final PeerId peerId;

  @Getter
  protected final Aer votedAmount;

  /**
   * Vote constructor.
   *
   * @param peerId a peer id
   * @param votedAmount an voted amount
   */
  public BlockProducer(final PeerId peerId, final Aer votedAmount) {
    assertNotNull(peerId, "Peer id must not null");
    assertNotNull(votedAmount, "Voted amount must not null");
    this.peerId = peerId;
    this.votedAmount = votedAmount;
  }

}
