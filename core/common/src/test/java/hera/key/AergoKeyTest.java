/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.KeyFormat;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  protected final ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);

  @Test
  public void testExportAndImportWif() throws Exception {
    final AergoKeyGenerator generator = new AergoKeyGenerator();
    final String passphrase = "password";
    for (int i = 0; i < 10; ++i) {
      final AergoKey expected = generator.create();
      final EncryptedPrivateKey wif = expected.exportAsWif(passphrase);
      final AergoKey actual = AergoKey.of(wif, passphrase);
      assertEquals(expected, actual);
    }
  }
  @Test
  public void testExportAndImportKeyFormat() throws Exception {
    final AergoKeyGenerator generator = new AergoKeyGenerator();
    final String passphrase = "password";
    for (int i = 0; i < 10; ++i) {
      final AergoKey expected = generator.create();
      final KeyFormat keyFormat = expected.exportAsKeyFormat(passphrase);
      final AergoKey actual = AergoKey.of(keyFormat, passphrase);
      assertEquals(expected, actual);
    }
  }

  @Test
  public void testSignAndVerifyTransaction() throws Exception {
    final AergoSignVerifier verifier = new AergoSignVerifier();
    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
          .from(key.getAddress())
          .to(key.getAddress())
          .amount("10000", Unit.AER)
          .nonce(1L)
          .build();
      final Transaction signedTransaction = key.sign(rawTransaction);
      assertTrue(verifier.verify(signedTransaction));
    }
  }

  @Test
  public void testSignAndVerifyMessageInBytesValue() throws Exception {
    final AergoSignVerifier verifier = new AergoSignVerifier();
    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final BytesValue message = BytesValue.of(randomUUID().toString().getBytes());
      final Signature signature = key.signMessage(message);
      assertTrue(verifier.verify(key.getAddress(), message, signature));
    }
  }

  @Test
  public void testSignOnLargeMessage() throws Exception {
    final AergoSignVerifier verifier = new AergoSignVerifier();
    final String large1 = IoUtils.from(new InputStreamReader(open("largeone")));
    final String large2 = IoUtils.from(new InputStreamReader(open("large-with-one-char-diff")));

    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final Signature signature1 = key.signMessage(BytesValue.of(large1.getBytes()));
      final Signature signature2 = key.signMessage(BytesValue.of(large2.getBytes()));
      logger.debug("Sign for one: {}", signature1);
      logger.debug("Sign for two: {}", signature2);
      assertTrue(verifier.verify(key.getAddress(), BytesValue.of(large1.getBytes()), signature1));
      assertTrue(verifier.verify(key.getAddress(), BytesValue.of(large2.getBytes()), signature2));
      assertNotEquals(signature1, signature2);
    }
  }

}
