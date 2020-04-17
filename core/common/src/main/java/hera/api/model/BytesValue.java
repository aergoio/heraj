/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Decoder;
import hera.api.encode.Encoder;
import hera.exception.HerajException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;

@ApiAudience.Public
@ApiStability.Unstable
public class BytesValue {

  public static final BytesValue EMPTY = new BytesValue(new byte[0]);

  /**
   * Create {@code BytesValue} with a raw bytes array.
   *
   * @param bytes a raw bytes value
   * @return created {@link BytesValue}
   */
  public static BytesValue of(final byte[] bytes) {
    return new BytesValue(bytes);
  }

  /**
   * Create {@code BytesValue} with an encoded one.
   *
   * @param encoded an encoded one
   * @param decoder a decoder
   * @return created {@link BytesValue}
   */
  public static BytesValue of(final String encoded, final Decoder decoder) {
    return new BytesValue(encoded, decoder);
  }

  protected transient int hash;

  protected final byte[] value;

  /**
   * Create {@code BytesValue} with a raw bytes array.
   *
   * @param bytes a raw bytes value
   */
  public BytesValue(final byte[] bytes) {
    assertNotNull(bytes, "Raw bytes must not null");
    this.value = Arrays.copyOf(bytes, bytes.length);
  }

  /**
   * Create {@code BytesValue} with an encoded one.
   *
   * @param encoded an encoded one
   * @param decoder a decoder
   */
  public BytesValue(final String encoded, final Decoder decoder) {
    assertNotNull(encoded, "An encoded value must not null");
    assertNotNull(decoder, "A decoder must not null");
    try {
      this.value = from(decoder.decode(new StringReader(encoded)));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  public InputStream getInputStream() {
    return new ByteArrayInputStream(getValue());
  }

  public byte[] getValue() {
    return Arrays.copyOf(value, value.length);
  }

  public int length() {
    return value.length;
  }

  public boolean isEmpty() {
    return 0 == value.length;
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
  public String toString() {
    return new String(this.value);
  }

  /**
   * Get encoded value.
   *
   * @param encoder an encoder
   * @return an encoded value
   */
  public String getEncoded(final Encoder encoder) {
    try {
      assertNotNull(encoder, "An encoder must not null");
      return from(encoder.encode(getInputStream()));
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
