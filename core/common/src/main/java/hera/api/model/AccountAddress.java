/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.BytesValueUtils.trimPrefix;
import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.api.encode.Encoder;
import hera.exception.HerajException;
import hera.util.Adaptor;
import hera.util.BytesValueUtils;
import java.io.StringReader;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class AccountAddress implements Identity, Adaptor {

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  // which is equivalent with s compressed public key (see also X9.62 s 4.2.1)
  public static final int ADDRESS_BYTE_LENGTH = 33;
  public static final byte ADDRESS_PREFIX = 0x42;

  public static final AccountAddress EMPTY = new AccountAddress();

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded account address
   * @return created {@link AccountAddress}
   */
  public static AccountAddress of(final String encoded) {
    return new AccountAddress(encoded);
  }

  /**
   * Create {@code AccountAddress} with a bytes value. Note that bytes value doesn't have a prefix.
   *
   * @param bytesValue a bytes value
   * @return created {@link AccountAddress}
   */
  public static AccountAddress of(final BytesValue bytesValue) {
    return new AccountAddress(bytesValue);
  }

  // holds base58 with checksum encoded value including prefix
  protected transient String encoded;
  @Getter

  protected final BytesValue bytesValue;

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded account address
   */
  public AccountAddress(final String encoded) {
    assertNotNull(encoded, "Encoded address must not null");
    try {
      final Decoder decoder = Decoder.Base58Check;
      final byte[] raw = from(decoder.decode(new StringReader(encoded)));
      final BytesValue withPrefix = BytesValue.of(raw);
      if (!hasPrefix(withPrefix)) {
        throw new HerajException("Decoded address value must have prefix " + ADDRESS_PREFIX);
      }

      final BytesValue withoutPrefix = trimPrefix(withPrefix);
      if (!isValid(withoutPrefix)) {
        throw new HerajException("Raw bytes value length must be " + ADDRESS_BYTE_LENGTH
            + ", but was " + withoutPrefix.length());
      }

      this.encoded = encoded;
      this.bytesValue = withoutPrefix;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Create {@code AccountAddress} with a bytes value. Note that bytes value doesn't have a prefix.
   *
   * @param bytesValue a bytes value
   */
  public AccountAddress(final BytesValue bytesValue) {
    assertNotNull(bytesValue, "Address in bytesValue must not null");
    if (!isValid(bytesValue)) {
      throw new HerajException("Raw bytes value length must be " + ADDRESS_BYTE_LENGTH
          + ", but was " + bytesValue.length());
    }
    this.bytesValue = bytesValue;
  }

  protected AccountAddress() {
    this.bytesValue = BytesValue.EMPTY;
  }

  protected boolean hasPrefix(final BytesValue bytesValue) {
    return BytesValueUtils.validatePrefix(bytesValue, ADDRESS_PREFIX);
  }

  protected boolean isValid(final BytesValue rawAddress) {
    return rawAddress.length() == ADDRESS_BYTE_LENGTH;
  }

  @Override
  public String toString() {
    return getEncoded();
  }

  public String getEncoded() {
    return getValue();
  }

  @Override
  public String getValue() {
    if (null == this.encoded) {
      synchronized (this) {
        if (null == this.encoded) {
          try {
            final BytesValue withPrefix = BytesValueUtils.append(getBytesValue(), ADDRESS_PREFIX);
            final Encoder encoder = Encoder.Base58Check;
            this.encoded = from(encoder.encode(withPrefix.getInputStream()));
          } catch (Exception e) {
            throw new HerajException(e);
          }
        }
      }
    }
    return this.encoded;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (T) ContractAddress.of(getBytesValue());
    }
    return null;
  }

}
