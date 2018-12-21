/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.util.Base58Utils;
import org.junit.Test;

public class ContractTxHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() {
    final ContractTxHash contractTxHash = ContractTxHash.of(encodedHash);
    assertEquals(ContractTxHash.of(encodedHash), contractTxHash.adapt(Hash.class));
    assertEquals(BlockHash.of(encodedHash), contractTxHash.adapt(BlockHash.class));
    assertEquals(ContractTxHash.of(encodedHash), contractTxHash.adapt(TxHash.class));
    assertEquals(ContractTxHash.of(encodedHash), contractTxHash.adapt(ContractTxHash.class));
  }

}
