/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.IoUtils.from;

import hera.api.Encoder;
import hera.util.Adaptor;
import hera.util.Base58Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Getter;

public class BytesValue implements Supplier<InputStream>, Adaptor {

  /**
   * Factory method.
   *
   * @param bytes value
   * @return created {@link BytesValue}
   */
  public static BytesValue of(final byte[] bytes) {
    if (null == bytes) {
      return new BytesValue(null);
    }
    return new BytesValue(Arrays.copyOf(bytes, bytes.length));
  }

  public BytesValue(final byte[] bytes) {
    this.value = Optional.ofNullable(bytes).orElse(new byte[0]);
  }

  protected transient int hash;

  @Getter
  protected final byte[] value;

  public String getEncodedValue() throws IOException {
    return getEncodedValue(Encoder.defaultEncoder);
  }

  /**
   * Get encoded bytes value with a provided encoder.
   *
   * @param encoder encoder
   * @return encoded bytes value if an encoder isn't null. Otherwise, null
   * @throws IOException when encoding error occurs
   */
  public String getEncodedValue(final Encoder encoder) throws IOException {
    if (null == encoder) {
      return null;
    }
    return from(encoder.encode(new ByteArrayInputStream(value)));
  }

  @Override
  public String toString() {
    return Base58Utils.encode(value);
  }

  @Override
  public int hashCode() {
    int h = this.hash;
    if (h == 0 && this.value.length > 0) {
      for (final byte byteValue : this.value) {
        h = 31 * h + byteValue;
      }

      this.hash = h;
    }
    return h;
  }

  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final BytesValue other = (BytesValue) obj;
    return Arrays.equals(this.value, other.value);
  }

  @Override
  public InputStream get() {
    return new ByteArrayInputStream(getValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(BytesValue.class)) {
      return (Optional<T>) Optional.of(this);
    }
    return Optional.empty();
  }

}
