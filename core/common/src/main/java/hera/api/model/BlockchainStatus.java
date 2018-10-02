/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BlockchainStatus {

  @Getter
  @Setter
  protected long bestHeight;

  @Getter
  @Setter
  protected BlockHash bestBlockHash = new BlockHash(BytesValue.EMPTY);
}
