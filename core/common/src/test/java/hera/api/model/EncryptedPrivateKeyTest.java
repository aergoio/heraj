/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import java.io.IOException;
import org.junit.Test;

public class EncryptedPrivateKeyTest {

  public static final String ENCODED_ENCRYPTED_PRIVATE_KEY =
      "4821ya4VmaLhw8Lyd8G6Ktc1ooFBpzrCjDbsZdvqddQMEfCFJZX28PRaBSxnct6rCgTowPToe";

  public static final String ENCODED_ENCRYPTED_PRIVATE_KEY_WITHOUT_VERSION =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testCreation() throws IOException {
    final EncryptedPrivateKey encryptedPrivateKey =
        EncryptedPrivateKey.of(() -> ENCODED_ENCRYPTED_PRIVATE_KEY);
    assertNotNull(encryptedPrivateKey);
  }

  @Test(expected = InvalidVersionException.class)
  public void testCreationWithInvalidVersion() throws IOException {
    EncryptedPrivateKey.of(() -> ENCODED_ENCRYPTED_PRIVATE_KEY_WITHOUT_VERSION);
  }

}
