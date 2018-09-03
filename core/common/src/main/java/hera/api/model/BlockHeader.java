/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BlockHeader {
  @Getter
  @Setter
  protected BlockHash hash = new BlockHash(null);

  @Getter
  @Setter
  protected BlockHash previousHash = new BlockHash(null);

  @Getter
  @Setter
  protected long blockNumber;

  @Getter
  @Setter
  protected long timestamp;

  @Getter
  @Setter
  protected BlockHash rootHash = new BlockHash(null);

  @Getter
  @Setter
  protected TxHash txRootHash = new TxHash(null);

  @Getter
  @Setter
  protected Hash publicKey = new Hash(null);

  @Getter
  @Setter
  protected Hash sign = new Hash(null);
}
