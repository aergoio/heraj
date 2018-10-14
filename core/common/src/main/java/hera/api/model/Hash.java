/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.EncodedString;
import hera.exception.HerajException;
import hera.util.Adaptor;
import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Hash implements Adaptor {

  /**
   * Create {@code Hash} with an encoded value.
   *
   * @param encoded an encoded value
   * @return created {@link Hash}
   */
  public static Hash of(final EncodedString encoded) {
    return new Hash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link Hash}
   */
  public static Hash of(final BytesValue bytesValue) {
    return new Hash(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * Hash constructor.
   *
   * @param encoded an encoded value
   */
  public Hash(final EncodedString encoded) {
    try {
      bytesValue = encoded.decode();
    } catch (IOException e) {
      throw new HerajException(e);
    }
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
    return Base58Utils.encode(bytesValue.getValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(Hash.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (Optional<T>) Optional.ofNullable(BlockHash.of(getBytesValue()));
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (Optional<T>) Optional.ofNullable(ContractTxHash.of(getBytesValue()));
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (Optional<T>) Optional.ofNullable(TxHash.of(getBytesValue()));
    }
    return Optional.empty();
  }

}
