/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58;
import hera.util.Base58Utils;
import java.io.IOException;
import org.junit.Test;

public class TxHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() throws IOException {
    final Base58 encoded = () -> encodedHash;
    final TxHash txHash = TxHash.of(encoded);
    assertEquals((Hash) TxHash.of(encoded), txHash.adapt(Hash.class).get());
    assertEquals(BlockHash.of(encoded), txHash.adapt(BlockHash.class).get());
    assertEquals(TxHash.of(encoded), txHash.adapt(TxHash.class).get());
    assertEquals(ContractTxHash.of(encoded), txHash.adapt(ContractTxHash.class).get());
  }

}
