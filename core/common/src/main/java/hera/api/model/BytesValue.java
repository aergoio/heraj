/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

@ApiAudience.Public
@ApiStability.Unstable
public class BytesValue {

  public static final BytesValue EMPTY = new BytesValue(null);

  /**
   * Create {@code BytesValue} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link BytesValue}
   */
  @ApiAudience.Public
  public static BytesValue of(final byte[] bytes) {
    return new BytesValue(bytes);
  }

  protected transient int hash;

  protected final byte[] value;

  /**
   * BytesValue} constructor.
   *
   * @param bytes value
   */
  @ApiAudience.Public
  public BytesValue(final byte[] bytes) {
    this.value = bytes != null ? Arrays.copyOf(bytes, bytes.length) : new byte[0];
  }

  public byte[] getValue() {
    return Arrays.copyOf(value, value.length);
  }

  public boolean isEmpty() {
    return 0 == value.length;
  }

  @Override
  public String toString() {
    return null == value ? "" : new String(value);
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

  public InputStream getInputStream() {
    return new ByteArrayInputStream(getValue());
  }

}
