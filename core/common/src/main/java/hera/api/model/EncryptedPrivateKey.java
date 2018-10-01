/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.exception.HerajException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class EncryptedPrivateKey extends BytesValue {

  public static final byte PRIVATE_KEY_VERSION = (byte) 0xAA;

  /**
   * Create {@code EncryptedPrivateKey} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link EncryptedPrivateKey}
   */
  public static EncryptedPrivateKey of(final byte[] bytes) {
    return of(bytes, EncryptedPrivateKey::new);
  }

  /**
   * Create {@code EncryptedPrivateKey} with a base58 with checksum encoded value.
   *
   * @param encoded base58 with checksum encoded value
   * @return created {@link EncryptedPrivateKey}
   */
  public static EncryptedPrivateKey of(final String encoded) {
    return of(encoded, e -> {
      final byte[] withVersion = Base58Utils.decodeWithCheck(encoded);
      if (PRIVATE_KEY_VERSION != withVersion[0]) {
        throw new HerajException("Invalid private key version");
      }
      return Arrays.copyOfRange(withVersion, 1, withVersion.length);
    }, EncryptedPrivateKey::new);
  }

  public EncryptedPrivateKey(final byte[] value) {
    super(value);
  }

  @Override
  public String getEncodedValue() throws IOException {
    final byte[] withVersion = new byte[value.length + 1];
    withVersion[0] = PRIVATE_KEY_VERSION;
    System.arraycopy(value, 0, withVersion, 1, value.length);
    return Base58Utils.encodeWithCheck(withVersion);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(EncryptedPrivateKey.class)) {
      return (Optional<T>) Optional.of(this);
    }
    return Optional.empty();
  }

}
