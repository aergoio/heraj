/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import java.util.List;

public interface BlockOperation {

  /**
   * Get block.
   *
   * @param hash block's hash
   * @return block
   */
  Block getBlock(Hash hash);

  /**
   * Get block headers of size starting from block for provided hash.
   *
   * @param hash block's hash
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockHeader> listBlockHeaders(Hash hash, int size);

  /**
   * Get block headers of size starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockHeader> listBlockHeaders(long height, int size);
}
