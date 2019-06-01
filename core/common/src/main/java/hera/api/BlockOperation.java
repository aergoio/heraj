/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import java.util.List;

/**
 * Provide block related operations. It provides followings:
 *
 * <ul>
 * <li>lookup block metadata</li>
 * <li>lookup block</li>
 * </ul>
 *
 * @author bylee, Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface BlockOperation {

  /**
   * Get block meta data by hash.
   *
   * @param blockHash block hash
   * @return block meta data
   */
  BlockMetadata getBlockMetadata(BlockHash blockHash);

  /**
   * Get block meta data by height.
   *
   * @param height block's height
   * @return block meta data
   */
  BlockMetadata getBlockMetadata(long height);

  /**
   * Get list of block meta data of {@code size} backward starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return list of block meta data
   */
  List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size);

  /**
   * Get list of block meta data of {@code size} backward starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return list of block meta data
   */
  List<BlockMetadata> listBlockMetadatas(long height, int size);

  /**
   * Get block by hash.
   *
   * @param blockHash block hash
   * @return block
   */
  Block getBlock(BlockHash blockHash);

  /**
   * Get block by height.
   *
   * @param height block's height
   * @return block
   */
  Block getBlock(long height);

}
