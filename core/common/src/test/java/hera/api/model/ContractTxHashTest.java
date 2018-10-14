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

public class ContractTxHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() throws IOException {
    final Base58 encoded = () -> encodedHash;
    final ContractTxHash contractTxHash = ContractTxHash.of(encoded);
    assertEquals((Hash) ContractTxHash.of(encoded), contractTxHash.adapt(Hash.class).get());
    assertEquals(BlockHash.of(encoded), contractTxHash.adapt(BlockHash.class).get());
    assertEquals((TxHash) ContractTxHash.of(encoded), contractTxHash.adapt(TxHash.class).get());
    assertEquals(ContractTxHash.of(encoded), contractTxHash.adapt(ContractTxHash.class).get());
  }

}
