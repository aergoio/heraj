/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BlockAsyncOperation {

  /**
   * Get block by hash asynchronously.
   *
   * @param hash block's hash
   * @return block
   */
  CompletableFuture<Block> getBlock(Hash hash);

  /**
   * Get block by height asynchronously.
   *
   * @param height block's height
   * @return block
   */
  CompletableFuture<Block> getBlock(long height);

  /**
   * Get block headers of size starting from block for provided hash asynchronously.
   *
   * @param hash block's hash
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  CompletableFuture<List<BlockHeader>> listBlockHeaders(Hash hash, int size);

  /**
   * Get block headers of size starting from block for provided height asynchronously.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  CompletableFuture<List<BlockHeader>> listBlockHeaders(long height, int size);
}
