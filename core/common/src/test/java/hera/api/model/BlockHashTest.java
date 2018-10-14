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

public class BlockHashTest {

  protected final String encodedHash = Base58Utils.encode(randomUUID().toString().getBytes());

  @Test
  public void testAdapt() throws IOException {
    final Base58 encoded = () -> encodedHash;
    final BlockHash blockHash = BlockHash.of(encoded);
    assertEquals((Hash) BlockHash.of(encoded), blockHash.adapt(Hash.class).get());
    assertEquals(BlockHash.of(encoded), blockHash.adapt(BlockHash.class).get());
    assertEquals(TxHash.of(encoded), blockHash.adapt(TxHash.class).get());
    assertEquals(ContractTxHash.of(encoded), blockHash.adapt(ContractTxHash.class).get());
  }

}
