/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface BlockAsyncOperation {

  /**
   * Get block by hash asynchronously.
   *
   * @param blockHash block hash
   * @return future of block or error
   */
  ResultOrErrorFuture<Block> getBlock(BlockHash blockHash);

  /**
   * Get block by height asynchronously.
   *
   * @param height block's height
   * @return future of block or error
   */
  ResultOrErrorFuture<Block> getBlock(long height);

  /**
   * Get block headers of size starting from block for provided hash asynchronously.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return future of block list or error
   */
  ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size);

  /**
   * Get block headers of size starting from block for provided height asynchronously.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return future of block list or error
   */
  ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(long height, int size);
}
