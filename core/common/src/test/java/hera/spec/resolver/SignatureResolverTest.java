/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.Signature;
import hera.util.HexUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.util.Arrays;
import org.junit.Test;

public class SignatureResolverTest extends AbstractTestCase {

  @Test
  public void testSerialize() throws Exception {
    final ECDSAKey key = new ECDSAKeyGenerator().create();
    final ECDSASignature ecdsaSignature = ECDSASignature.of(
        new BigInteger(
            "77742016982977049819968937189730099006007209897399569418319639670259283246582"),
        new BigInteger(
            "24080111729304174841921585755879357193051484773881703660717104599905026449822"));
    final Signature signature =
        SignatureResolver.serialize(ecdsaSignature, key.getParams().getN());
    assertTrue(Arrays.equals(HexUtils.decode(
        "3045022100ABE06C1B99DE0C51B4790D24EE52674F532D9057744ED9EEF3F61425F9D1BDF60220353CDC395B12ABB6E297085B4D6F1A9DF7783DB66F95A7E0CE28246FC538219E"),
        signature.getSign().getValue()));
  }

  @Test
  public void testSerializeAndParse() throws Exception {
    final ECDSAKey key = new ECDSAKeyGenerator().create();
    final ECDSASignature expected = ECDSASignature.of(
        new BigInteger(
            "77742016982977049819968937189730099006007209897399569418319639670259283246582"),
        new BigInteger(
            "24080111729304174841921585755879357193051484773881703660717104599905026449822"));
    final Signature signature = SignatureResolver.serialize(expected, key.getParams().getN());
    final ECDSASignature actual = SignatureResolver.parse(signature, key.getParams().getN());
    assertEquals(expected, actual);
  }


}
