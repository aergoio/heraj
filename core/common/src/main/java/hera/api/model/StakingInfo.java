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
public class StakingInfo {

  @Getter
  protected final AccountAddress address;

  @Getter
  protected final Aer amount;

  @Getter
  protected final long blockNumber;

  /**
   * StakingInfo constructor.
   *
   * @param address an account address
   * @param amount a staking amount. Must be &gt;= 0
   * @param blockNumber a staking block number
   */
  @ApiAudience.Private
  public StakingInfo(final AccountAddress address, final Aer amount, final long blockNumber) {
    assertNotNull(address, "Staking address must not null");
    assertNotNull(amount, "Staking amount must not null");
    this.address = address;
    this.amount = amount;
    this.blockNumber = blockNumber;
  }

}
