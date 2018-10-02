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
  protected BlockHash hash = new BlockHash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected BlockHash previousHash = new BlockHash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected long blockNumber;

  @Getter
  @Setter
  protected long timestamp;

  @Getter
  @Setter
  protected BlockHash rootHash = new BlockHash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected TxHash txRootHash = new TxHash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected Hash publicKey = new Hash(BytesValue.EMPTY);

  @Getter
  @Setter
  protected Hash sign = new Hash(BytesValue.EMPTY);

}
