/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class BlockchainStatus {

  @Getter
  protected long bestHeight;

  @Getter
  protected BlockHash bestBlockHash;

  /**
   * BlockchainStatus constructor.
   *
   * @param bestHeight a best block height. Must be &gt;= 0
   * @param bestBlockHash a best block hash
   */
  @ApiAudience.Private
  public BlockchainStatus(final long bestHeight, final BlockHash bestBlockHash) {
    assertTrue(bestHeight >= 0, "Best block height must be >= 0");
    assertNotNull(bestBlockHash, "Best block hash must not null");
    this.bestHeight = bestHeight;
    this.bestBlockHash = bestBlockHash;
  }

}
