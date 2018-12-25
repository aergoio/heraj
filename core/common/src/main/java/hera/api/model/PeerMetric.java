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
public class PeerMetric {

  @Getter
  protected String peerId;

  @Getter
  protected long sumIn;

  @Getter
  protected long averageIn;

  @Getter
  protected long sumOut;

  @Getter
  protected long averageOut;

  /**
   * PeerMetric constructor.
   * 
   * @param peerId a peer id
   * @param sumIn sum of in counts
   * @param averageIn average of in counts
   * @param sumOut sum of out counts
   * @param averageOut average of out counts
   */
  @ApiAudience.Private
  public PeerMetric(final String peerId, final long sumIn, final long averageIn, final long sumOut,
      final long averageOut) {
    assertNotNull(peerId, "Peer id must not null");
    this.peerId = peerId;
    this.sumIn = sumIn;
    this.averageIn = averageIn;
    this.sumOut = sumOut;
    this.averageOut = averageOut;
  }

}
