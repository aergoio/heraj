/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import org.junit.Test;

public class EncryptedPrivateKeyTest {

  public final String encodedEncryptedPrivateKey =
      "4821ya4VmaLhw8Lyd8G6Ktc1ooFBpzrCjDbsZdvqddQMEfCFJZX28PRaBSxnct6rCgTowPToe";

  public final String encodedEncryptedPrivateKeyWithoutVersion =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testCreation() {
    final EncryptedPrivateKey encryptedPrivateKey =
        EncryptedPrivateKey.of(encodedEncryptedPrivateKey);
    assertNotNull(encryptedPrivateKey);
  }

  @Test(expected = InvalidVersionException.class)
  public void testCreationWithInvalidVersion() {
    EncryptedPrivateKey.of(encodedEncryptedPrivateKeyWithoutVersion);
  }

}
