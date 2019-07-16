/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.EncryptedPrivateKey;
import hera.spec.resolver.EncryptedPrivateKeySpec;
import org.junit.Test;
import types.Rpc;

public class EncryptedPrivateKeyConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<EncryptedPrivateKey, Rpc.SingleBytes> converter =
        new EncryptedPrivateKeyConverterFactory().create();

    final EncryptedPrivateKey expected =
        new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKeySpec.PREFIX, 0x01}));
    final Rpc.SingleBytes rpcKey = converter.convertToRpcModel(expected);
    final EncryptedPrivateKey actual = converter.convertToDomainModel(rpcKey);
    assertEquals(expected, actual);
  }

}
