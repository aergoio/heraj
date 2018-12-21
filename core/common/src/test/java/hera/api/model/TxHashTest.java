/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.util.Base58Utils;
import org.junit.Test;

public class TxHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() {
    final TxHash txHash = TxHash.of(encodedHash);
    assertEquals(TxHash.of(encodedHash), txHash.adapt(Hash.class));
    assertEquals(BlockHash.of(encodedHash), txHash.adapt(BlockHash.class));
    assertEquals(TxHash.of(encodedHash), txHash.adapt(TxHash.class));
    assertEquals(ContractTxHash.of(encodedHash), txHash.adapt(ContractTxHash.class));
  }

}
