/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface BlockEitherOperation extends ContextAware {

  /**
   * Get block by hash.
   *
   * @param blockHash block hash
   * @return block or error
   */
  ResultOrError<Block> getBlock(BlockHash blockHash);

  /**
   * Get block by height.
   *
   * @param height block's height
   * @return block or error
   */
  ResultOrError<Block> getBlock(long height);

  /**
   * Get block headers of size starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return block list or error
   */
  ResultOrError<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size);

  /**
   * Get block headers of size starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list or error
   */
  ResultOrError<List<BlockHeader>> listBlockHeaders(long height, int size);
}
