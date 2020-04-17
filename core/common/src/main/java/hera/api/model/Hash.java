/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.api.encode.Encoder;
import hera.util.Adaptor;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class Hash implements Adaptor, Encodable {

  /**
   * Create {@code Hash} with a base58 encoded one.
   *
   * @param encoded a base58 encoded hash
   * @return created {@link Hash}
   */
  public static Hash of(final String encoded) {
    return new Hash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue a bytesValue
   * @return created {@link Hash}
   */
  public static Hash of(final BytesValue bytesValue) {
    return new Hash(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * Create {@code Hash} with a base58 encoded one.
   *
   * @param encoded a base58 encoded hash
   */
  public Hash(final String encoded) {
    assertNotNull(encoded, "Encoded value must not null");
    this.bytesValue = BytesValue.of(encoded, Decoder.Base58);
  }

  /**
   * Create {@code Hash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   */
  public Hash(final BytesValue bytesValue) {
    assertNotNull(bytesValue, "Bytes value must not null");
    this.bytesValue = bytesValue;
  }

  @Override
  public String getEncoded() {
    return getBytesValue().getEncoded(Encoder.Base58);
  }

  @Override
  public int hashCode() {
    return getBytesValue().hashCode();
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
    return getBytesValue().equals(other.getBytesValue());
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
