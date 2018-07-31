/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;

public class BlockHeader {
  @Getter
  @Setter
  protected Hash hash;

  @Getter
  @Setter
  protected Hash previousBlockHash;

  @Getter
  @Setter
  protected long blockNumber;

  @Getter
  @Setter
  protected long timestamp;

  @Getter
  @Setter
  protected Hash rootHash;

  @Getter
  @Setter
  protected Hash transactionsRootHash;

  @Getter
  @Setter
  protected Hash publicKey;

  @Getter
  @Setter
  protected Hash sign;
}
