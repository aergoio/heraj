/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58;
import hera.util.Base58Utils;
import org.junit.Test;

public class HashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() {
    final Base58 encoded = () -> encodedHash;
    final Hash hash = Hash.of(encoded);
    assertEquals(Hash.of(encoded), hash.adapt(Hash.class).get());
    assertEquals(BlockHash.of(encoded), hash.adapt(BlockHash.class).get());
    assertEquals(TxHash.of(encoded), hash.adapt(TxHash.class).get());
    assertEquals(ContractTxHash.of(encoded), hash.adapt(ContractTxHash.class).get());
  }

}
