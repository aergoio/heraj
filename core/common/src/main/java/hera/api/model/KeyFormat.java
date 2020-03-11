/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.IoUtils.from;
import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.io.IOException;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class KeyFormat implements Encrypted {

  /**
   * Create a {@code KeyFile} with an input stream.
   *
   * @param inputStream an input stream for key file
   * @return created  {@link KeyFormat}
   */
  public static KeyFormat of(final InputStream inputStream) {
    return new KeyFormat(inputStream);
  }

  /**
   * Create a {@code KeyFile} with a bytesValue.
   *
   * @param bytesValue a key file bytesValue
   * @return created  {@link KeyFormat}
   */
  public static KeyFormat of(final BytesValue bytesValue) {
    return new KeyFormat(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * Create a {@code KeyFile} with an input stream.
   *
   * @param inputStream an input stream for key file
   */
  public KeyFormat(final InputStream inputStream) {
    try {
      assertNotNull(inputStream, "InputStream must not null");
      this.bytesValue = BytesValue.of(from(inputStream));
    } catch (IOException e) {
      throw new HerajException(e);
    }
  }

  /**
   * Create a {@code KeyFile} with a bytesValue.
   *
   * @param bytesValue a key file bytesValue
   */
  public KeyFormat(final BytesValue bytesValue) {
    assertNotNull(bytesValue, "BytesValue must not null");
    this.bytesValue = bytesValue;
  }

}
