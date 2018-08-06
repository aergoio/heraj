/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;

public class BlockHeader {
  @Getter
  @Setter
  protected Hash hash = new Hash(null);

  @Getter
  @Setter
  protected Hash previousBlockHash = new Hash(null);

  @Getter
  @Setter
  protected long blockNumber;

  @Getter
  @Setter
  protected long timestamp;

  @Getter
  @Setter
  protected Hash rootHash = new Hash(null);

  @Getter
  @Setter
  protected Hash transactionsRootHash = new Hash(null);

  @Getter
  @Setter
  protected Hash publicKey = new Hash(null);

  @Getter
  @Setter
  protected Hash sign = new Hash(null);
}
