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
public class VotingInfo {

  @Getter
  protected final PeerId peerId;

  @Getter
  protected final Aer amount;

  /**
   * VotingInfo constructor.
   *
   * @param peerId a peer id
   * @param amount an amount
   */
  public VotingInfo(final PeerId peerId, final Aer amount) {
    assertNotNull(peerId, "Peer id must not null");
    assertNotNull(amount, "Amount must not null");
    this.peerId = peerId;
    this.amount = amount;
  }

}
