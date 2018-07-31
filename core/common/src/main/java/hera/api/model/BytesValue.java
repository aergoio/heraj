/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.Encoder;
import hera.util.HexUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BytesValue implements Supplier<InputStream> {

  /**
   * Factory method.
   *
   * @param bytes value
   *
   * @return created {@link BytesValue}
   */
  public static BytesValue of(final byte[] bytes) {
    return new BytesValue(Arrays.copyOf(bytes, bytes.length));
  }

  protected transient int hash;

  @Getter
  @NonNull
  protected final byte[] value;

  public String getEncodedValue() throws IOException {
    return getEncodedValue(Encoder.defaultEncoder);
  }

  public String getEncodedValue(final Encoder encoder) throws IOException {
    return encoder.encode(new ByteArrayInputStream(value)).toString();
  }

  @Override
  public String toString() {
    return HexUtils.encode(value);
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
}
