/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.spec.resolver.AddressResolver;
import hera.util.Adaptor;
import hera.util.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class AccountAddress implements Identity, Encodable, Adaptor {

  public static final AccountAddress EMPTY =
      new AccountAddress(BytesValue.EMPTY, StringUtils.EMPTY_STRING);

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link AccountAddress}
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static AccountAddress of(final String encoded) {
    return new AccountAddress(encoded);
  }

  @Getter
  protected final String value; // holds encoded value including prefix

  @Getter
  protected final BytesValue bytesValue; // holds raw bytes array without prefix

  /**
   * AccountAddress constructor.
   *
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public AccountAddress(final String encoded) {
    this(AddressResolver.convertToRaw(encoded), encoded);
  }

  /**
   * AccountAddress constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  @ApiAudience.Private
  public AccountAddress(final BytesValue bytesValue) {
    this(bytesValue, AddressResolver.convertToEncoded(bytesValue));
  }

  protected AccountAddress(final BytesValue bytesValue, final String value) {
    this.bytesValue = bytesValue;
    this.value = value;
  }

  @Override
  public String toString() {
    return getValue();
  }

  @Override
  public String getEncoded() {
    return getValue();
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
