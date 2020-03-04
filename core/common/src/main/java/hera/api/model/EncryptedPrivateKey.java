/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.api.encode.Encoder;
import hera.api.model.internal.BytesValueUtils;
import hera.exception.HerajException;
import java.io.StringReader;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class EncryptedPrivateKey implements Encrypted {

  public static final byte ENCRYPTED_PREFIX = (byte) 0xAA;

  public static final EncryptedPrivateKey EMPTY = new EncryptedPrivateKey();

  /**
   * Create {@code EncryptedPrivateKey} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link EncryptedPrivateKey}
   */
  public static EncryptedPrivateKey of(final String encoded) {
    return new EncryptedPrivateKey(encoded);
  }

  /**
   * Create {@code EncryptedPrivateKey} with a bytes value. Note that bytes value doesn't have a
   * prefix.
   *
   * @param bytesValue a bytes value
   * @return created {@link EncryptedPrivateKey}
   */
  public static EncryptedPrivateKey of(final BytesValue bytesValue) {
    return new EncryptedPrivateKey(bytesValue);
  }

  // holds base58 with checksum encoded value including prefix
  protected transient String encoded;

  @Getter
  protected final BytesValue bytesValue;

  /**
   * Create {@code EncryptedPrivateKey} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   */
  public EncryptedPrivateKey(final String encoded) {
    assertNotNull(encoded, "An encoded encrypted private key must not null");
    try {
      final Decoder decoder = Decoder.Base58Check;
      final BytesValue decoded = BytesValue.of(from(decoder.decode(new StringReader(encoded))));
      if (!isValid(decoded)) {
        throw new HerajException("Raw bytes value must have prefix " + ENCRYPTED_PREFIX);
      }
      this.encoded = encoded;
      this.bytesValue = decoded;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Create {@code EncryptedPrivateKey} with a bytes value. Note that bytes value doesn't have a
   * prefix.
   *
   * @param bytesValue a bytes value
   */
  public EncryptedPrivateKey(final BytesValue bytesValue) {
    assertNotNull(bytesValue, "An raw encrypted private key must not null");
    if (!isValid(bytesValue)) {
      throw new HerajException("Raw bytes value must have prefix " + ENCRYPTED_PREFIX);
    }
    this.bytesValue = bytesValue;
  }
  
  protected EncryptedPrivateKey() {
    this.bytesValue = BytesValue.EMPTY;
  }

  protected boolean isValid(final BytesValue rawEncryptedPrivateKey) {
    return BytesValueUtils.validatePrefix(rawEncryptedPrivateKey, ENCRYPTED_PREFIX);
  }

  @Override
  public String toString() {
    return getEncoded();
  }

  /**
   * Get base58 with checksum encoded value.
   *
   * @return a base58 with checksum encoded value
   */
  public String getEncoded() {
    if (null == this.encoded) {
      synchronized (this) {
        if (null == this.encoded) {
          try {
            final Encoder encoder = Encoder.Base58Check;
            this.encoded = from(encoder.encode(getBytesValue().getInputStream()));
          } catch (Exception e) {
            throw new HerajException(e);
          }
        }
      }
    }
    return this.encoded;
  }

}
