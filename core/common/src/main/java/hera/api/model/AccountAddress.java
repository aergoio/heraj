/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.EncodingUtils.decodeBase58WithCheck;
import static hera.util.EncodingUtils.encodeBase58WithCheck;
import static hera.util.EncodingUtils.encodeHexa;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.spec.AddressSpec;
import hera.spec.resolver.AddressResolver;
import hera.util.Adaptor;
import hera.util.VersionUtils;
import java.security.PublicKey;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class AccountAddress implements Identity, Encodable, Adaptor {

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
      VersionUtils.validate(bytesValue, AddressSpec.PREFIX);
    }
    this.bytesValue = bytesValue;
  }

  /**
   * Recover ECPublicKey from address.
   *
   * @return an ECPublicKey
   */
  public PublicKey asPublicKey() {
    return AddressResolver.recoverPublicKey(this);
  }

  @Override
  public String getValue() {
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (T) ContractAddress.of(getBytesValue());
    }
    return null;
  }

}
