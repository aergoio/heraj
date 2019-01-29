/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.EncodingUtils.decodeBase58WithCheck;
import static hera.util.EncodingUtils.decodeHexa;
import static hera.util.EncodingUtils.encodeBase58WithCheck;
import static hera.util.EncodingUtils.encodeHexa;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.util.VersionUtils;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class AccountAddress implements Encodable {

  public static final byte VERSION = 0x42;

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link AccountAddress}
   *
   * @throws DecodingFailureException if decoding failed
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Public
  public static AccountAddress of(final String encoded) {
    return new AccountAddress(encoded);
  }

  /**
   * Create {@code AccountAddress}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link AccountAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Private
  public static AccountAddress of(final BytesValue bytesValue) {
    return new AccountAddress(bytesValue);
  }

  /**
   * Create {@code AccountAddress} with an alias.
   *
   * @param alias an alias
   * @return created {@link AccountAddress}
   *
   * @throws DecodingFailureException if decoding failed
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Private
  public static AccountAddress fromAlias(final String alias) {
    return new AccountAddress(decodeHexa(alias));
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * AccountAddress constructor.
   *
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws DecodingFailureException if decoding failed
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Public
  public AccountAddress(final String encoded) {
    this(decodeBase58WithCheck(encoded));
  }

  /**
   * AccountAddress constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Private
  public AccountAddress(final BytesValue bytesValue) {
    if (BytesValue.EMPTY != bytesValue) {
      VersionUtils.validate(bytesValue, VERSION);
    }
    this.bytesValue = bytesValue;
  }

  /**
   * Get alias of account address.
   *
   * @return an alias of address
   */
  public String getAlias() {
    return encodeHexa(getBytesValue());
  }

  @Override
  public String getEncoded() {
    return encodeBase58WithCheck(getBytesValue());
  }

  @Override
  public int hashCode() {
    return bytesValue.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final AccountAddress other = (AccountAddress) obj;
    return bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return getEncoded();
  }

}
