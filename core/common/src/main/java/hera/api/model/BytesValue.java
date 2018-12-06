/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.HexUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Supplier;
import lombok.Getter;

public class BytesValue implements Supplier<InputStream> {

  public static final BytesValue EMPTY = new BytesValue(null);

  /**
   * Create {@code BytesValue} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link BytesValue}
   */
  public static BytesValue of(final byte[] bytes) {
    return new BytesValue(bytes);
  }

  protected transient int hash;

  @Getter
  protected final byte[] value;

  public BytesValue(final byte[] bytes) {
    this.value = bytes != null ? Arrays.copyOf(bytes, bytes.length) : new byte[0];
  }

  public boolean isEmpty() {
    return 0 == value.length;
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
