/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Block extends BlockHeader {

  @Getter
  @Setter
  protected List<Transaction> transactions;

  @Override
  public String toString() {
    return String.format("Block(%s, transactions=%s)", super.toString(), transactions.toString());
  }
}
