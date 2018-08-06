/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.Encoder;
import hera.util.HexUtils;
import hera.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BytesValue implements Supplier<InputStream> {

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

  protected transient int hash;

  @Getter
  protected final byte[] value;

  /**
   * Get encoded bytes value with a default encoder.
   *
   * @return encoded bytes value if a value isn't null. Otherwise, null
   */
  public String getEncodedValue() throws IOException {
    return getEncodedValue(Encoder.defaultEncoder);
  }

  /**
   * Get encoded bytes value with a provided encoder.
   *
   * @param encoder encoder
   * @return encoded bytes value if a value and an encoder isn't null. Otherwise, null
   */
  public String getEncodedValue(final Encoder encoder) throws IOException {
    if (null == value || null == encoder) {
      return null;
    }
    return encoder.encode(new ByteArrayInputStream(value)).toString();
  }

  @Override
  public String toString() {
    if (null == value) {
      return StringUtils.NULL_STRING;
    }
    return HexUtils.encode(value);
  }

  @Override
  public int hashCode() {
    if (null == this.value) {
      return 0;
    }
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
    if (null == getValue()) {
      return null;
    }
    return new ByteArrayInputStream(getValue());
  }
}
