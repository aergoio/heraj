/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;

public class BlockchainStatus {

  @Getter
  @Setter
  protected long bestHeight;

  @Getter
  @Setter
  protected Hash bestBlockHash;
}
