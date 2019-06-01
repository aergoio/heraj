/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class BlockMetadata {

  @Getter
  protected final BlockHash blockHash;

  @Getter
  protected final BlockHeader blockHeader;

  @Getter
  protected final int txCount;

  @Getter
  protected final long blockSize; // in bytes

  /**
   * BlockMetadata constructor.
   *
   * @param blockHash a block hash
   * @param blockHeader a block header
   * @param txCount tx count in a block
   * @param blockSize a block size
   */
  @ApiAudience.Private
  public BlockMetadata(BlockHash blockHash, BlockHeader blockHeader, int txCount, long blockSize) {
    this.blockHash = blockHash;
    this.blockHeader = blockHeader;
    this.txCount = txCount;
    this.blockSize = blockSize;
  }

}
