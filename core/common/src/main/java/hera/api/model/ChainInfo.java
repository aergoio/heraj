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
  protected final ChainId chainId;

  @Getter
  protected final int blockProducerCount;

  @Getter
  protected final long maxBlockSize; // bytes

  @Getter
  protected final Aer totalTokenAmount;

  @Getter
  protected final Aer minimumStakingAmount;

  @Getter
  protected final Aer totalStaked;

  @Getter
  protected final Aer gasPrice;

  @Getter
  protected final Aer namingPrice;

  /**
   * ChainInfo constructor.
   *
   * @param chainId a chain id
   * @param blockProducerCount a number of block producers
   * @param maxBlockSize max block size in bytes
   * @param totalTokenAmount a total aer token amount
   * @param minimumStakingAmount an minimun staking amount
   * @param totalStaked a total staked amount
   * @param gasPrice a gas price
   * @param namingPrice an naming price
   */
  @ApiAudience.Private
  public ChainInfo(final ChainId chainId, final int blockProducerCount, final long maxBlockSize,
      final Aer totalTokenAmount, final Aer minimumStakingAmount, final Aer totalStaked,
      final Aer gasPrice, final Aer namingPrice) {
    assertNotNull(chainId, "Chain id must not null");
    assertNotNull(totalTokenAmount, "Total token amount must not null");
    assertNotNull(minimumStakingAmount, "Minumum staking amount must not null");
    this.chainId = chainId;
    this.blockProducerCount = blockProducerCount;
    this.maxBlockSize = maxBlockSize;
    this.totalTokenAmount = totalTokenAmount;
    this.minimumStakingAmount = minimumStakingAmount;
    this.totalStaked = totalStaked;
    this.gasPrice = gasPrice;
    this.namingPrice = namingPrice;
  }

}
