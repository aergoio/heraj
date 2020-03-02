/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.util.Base58Utils;
import org.junit.Test;

public class EncryptedPrivateKeyTest {

  protected final String encoded =
      "47p451XCur4AdaxQF76h8FMt5ngxg7ewdrvhFj2J8qfFagc1musEVZf8LLV1kfNHVDDH27Mm6";

  public final String withoutVersion =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testOf() {
    final EncryptedPrivateKey encryptedPrivateKey = EncryptedPrivateKey.of(encoded);
    assertNotNull(encryptedPrivateKey);
  }

  @Test
  public void shouldOfThrowExceptionOnInvalidVersion() {
    try {
      EncryptedPrivateKey.of(withoutVersion);
    } catch (Exception e) {
      // then
    }
  }

}
