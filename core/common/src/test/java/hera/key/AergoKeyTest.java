/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.encode.Decoder;
import hera.api.encode.Encoder;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  protected static final ChainIdHash chainIdHash =
      new ChainIdHash(of(randomUUID().toString().getBytes()));

  private static final String ENCRYPTED_PRIVATE_KEY =
      "47RHxbUL3DhA1TMHksEPdVrhumcjdXLAB3Hkv61mqkC9M1Wncai5b91q7hpKydfFHKyyVvgKt";

  @Test
  public void testOfWithEncodedEncryptedPrivateKey() throws Exception {
    final String password = "password";
    final AergoKey key = AergoKey.of(ENCRYPTED_PRIVATE_KEY, password);
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

  @Test
  public void testGetEncryptedPrivateKey() throws Exception {
    final String password = "password";
    final AergoKey key = AergoKey.of(ENCRYPTED_PRIVATE_KEY, password);
    final String newEncryptedPrivateKey = key.export(password).toString();
    assertEquals(ENCRYPTED_PRIVATE_KEY, newEncryptedPrivateKey);
  }

  @Test
  public void testSignAndVerifyTransaction() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
          .from(key.getAddress())
          .to(key.getAddress())
          .amount("10000", Unit.AER)
          .nonce(1L)
          .build();
      final Transaction signedTransaction = key.sign(rawTransaction);
      assertTrue(key.verify(signedTransaction));
    }
  }

  @Test
  public void testSignAndVerifyMessageInBytesValue() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final BytesValue message = new BytesValue(randomUUID().toString().getBytes());
      final Signature signature = key.signMessage(message);
      assertTrue(key.verifyMessage(message, signature));
    }
  }

  @Test
  public void testSignAndVerifyMessageInString() throws Exception {
    Encoder[] encoders = new Encoder[] {Encoder.Hex, Encoder.Base58, Encoder.Base64};
    Decoder[] decoders = new Decoder[] {Decoder.Hex, Decoder.Base58, Decoder.Base64};

    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final String message = randomUUID().toString();
      for (int j = 0; j < encoders.length; ++j) {
        final Encoder encoder = encoders[j];
        final Decoder decoder = decoders[j];
        final String signature = key.signMessage(message, encoder);
        assertTrue(key.verifyMessage(message, signature, decoder));
      }
    }
  }

  @Test
  public void testSignOnLargeMessage() throws Exception {
    final String largeone = IoUtils.from(new InputStreamReader(open("largeone")));
    final String largetwo = IoUtils.from(new InputStreamReader(open("large-with-one-char-diff")));

    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final String signatureForone = key.signMessage(largeone);
      final String signatureFortwo = key.signMessage(largetwo);
      logger.debug("Sign for one: {}", signatureForone);
      logger.debug("Sign for two: {}", signatureFortwo);
      assertTrue(key.verifyMessage(largeone, signatureForone));
      assertTrue(key.verifyMessage(largetwo, signatureFortwo));
      assertNotEquals(signatureForone, signatureFortwo);
    }
  }

}
