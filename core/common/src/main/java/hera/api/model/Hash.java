/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.EncodingUtils.decodeBase58;
import static hera.util.EncodingUtils.encodeBase58;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.util.Adaptor;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class Hash implements Adaptor, Encodable {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded String with base58 encoded
   * @return created {@link Hash}
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static Hash of(final String encoded) {
    return new Hash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link Hash}
   */
  @ApiAudience.Private
  public static Hash of(final BytesValue bytesValue) {
    return new Hash(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * Hash constructor.
   *
   * @param encoded String with base58 encoded
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public Hash(final String encoded) {
    this(decodeBase58(encoded));
  }

  /**
   * Hash constructor.
   *
   * @param bytesValue an bytes value
   */
  @ApiAudience.Private
  public Hash(final BytesValue bytesValue) {
    this.bytesValue = null != bytesValue ? bytesValue : BytesValue.EMPTY;
  }

  @Override
  public String getEncoded() {
    return encodeBase58(getBytesValue());
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
    final Hash other = (Hash) obj;
    return bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return getEncoded();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(Hash.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (T) BlockHash.of(getBytesValue());
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (T) TxHash.of(getBytesValue());
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (T) ContractTxHash.of(getBytesValue());
    }
    return null;
  }
}
