/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.util.Base58Utils;
import org.junit.Test;

public class HashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() {
    final Hash hash = Hash.of(encodedHash);
    assertEquals(Hash.of(encodedHash), hash.adapt(Hash.class));
    assertEquals(BlockHash.of(encodedHash), hash.adapt(BlockHash.class));
    assertEquals(TxHash.of(encodedHash), hash.adapt(TxHash.class));
    assertEquals(ContractTxHash.of(encodedHash), hash.adapt(ContractTxHash.class));
  }

}
