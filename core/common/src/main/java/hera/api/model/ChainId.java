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
public class ChainId {

  @Getter
  protected String magic;

  @Getter
  protected boolean isPublic;

  @Getter
  protected boolean isMainNet;

  @Getter
  protected String consensus;

  /**
   * ChainId constructor.
   *
   * @param magic a magic of chain id
   * @param isPublic whether it's public or not
   * @param isMainNet whether it's mainnet or not
   * @param consensus a consensus type
   */
  @ApiAudience.Private
  public ChainId(final String magic, final boolean isPublic, final boolean isMainNet,
      final String consensus) {
    assertNotNull(magic, "Magis must not null");
    assertNotNull(consensus, "Consensus must not null");
    this.magic = magic;
    this.isPublic = isPublic;
    this.isMainNet = isMainNet;
    this.consensus = consensus;
  }

}
