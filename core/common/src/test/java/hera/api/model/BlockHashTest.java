/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.util.Base58Utils;
import org.junit.Test;

public class BlockHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() {
    final BlockHash blockHash = BlockHash.of(encodedHash);
    assertEquals(BlockHash.of(encodedHash), blockHash.adapt(Hash.class));
    assertEquals(BlockHash.of(encodedHash), blockHash.adapt(BlockHash.class));
    assertEquals(TxHash.of(encodedHash), blockHash.adapt(TxHash.class));
    assertEquals(ContractTxHash.of(encodedHash), blockHash.adapt(ContractTxHash.class));
  }

}
