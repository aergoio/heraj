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
public class ChainInfo {

  @Getter
  protected ChainId chainId;

  @Getter
  protected int blockProducerCount;

  @Getter
  protected long maxBlockSize; // bytes

  @Getter
  protected Aer totalTokenAmount;

  @Getter
  protected Aer minimumStakingAmount;

  /**
   * ChainInfo constructor.
   *
   * @param chainId a chain id
   * @param blockProducerCount a number of block producers
   * @param maxBlockSize max block size in bytes
   * @param totalTokenAmount a total aer token amount
   * @param minimumStakingAmount an minimun staking amount
   */
  @ApiAudience.Private
  public ChainInfo(final ChainId chainId, final int blockProducerCount, final long maxBlockSize,
      final Aer totalTokenAmount, final Aer minimumStakingAmount) {
    assertNotNull(chainId, "Chain id must not null");
    assertNotNull(totalTokenAmount, "Total token amount must not null");
    assertNotNull(minimumStakingAmount, "Minumum staking amount must not null");
    this.chainId = chainId;
    this.blockProducerCount = blockProducerCount;
    this.maxBlockSize = maxBlockSize;
    this.totalTokenAmount = totalTokenAmount;
    this.minimumStakingAmount = minimumStakingAmount;
  }

}
