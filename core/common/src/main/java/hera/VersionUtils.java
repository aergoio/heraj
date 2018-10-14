/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.api.model.BytesValue;
import hera.exception.InvalidVersionException;
import java.util.Arrays;

public class VersionUtils {

  /**
   * Validate the version from the {@code source}.
   *
   * @param source byte array to validate
   * @param version version byte
   * @throws InvalidVersionException if version mismatches
   */
  public static void validate(final BytesValue source, final byte version) {
    validate(source.getValue(), version);
  }

  /**
   * Validate the version from the {@code source}.
   *
   * @param source byte array to validate
   * @param version version byte
   * @throws InvalidVersionException if version mismatches
   */
  public static void validate(final byte[] source, final byte version) {
    if (null == source || 0 == source.length) {
      throw new InvalidVersionException("Bytes is empty");
    }
    if (version != source[0]) {
      throw new InvalidVersionException(version, source[0]);
    }
  }

  /**
   * Envelop the version to the given {@code source}.
   *
   * @param source envelop target
   * @param version version to envelop
   * @return version enveloped byte array
   */
  public static BytesValue envelop(final BytesValue source, final byte version) {
    return BytesValue.of(envelop(source.getValue(), version));
  }

  /**
   * Envelop the version to the given {@code source}.
   *
   * @param source envelop target
   * @param version version to envelop
   * @return version enveloped byte array
   */
  public static byte[] envelop(final byte[] source, final byte version) {
    if (null == source || 0 == source.length) {
      return new byte[0];
    }
    final byte[] withVersion = new byte[source.length + 1];
    withVersion[0] = version;
    System.arraycopy(source, 0, withVersion, 1, source.length);
    return withVersion;
  }

  /**
   * Trim version of the {@code source}.
   *
   * @param source byte array which may contain version.
   * @return byte array with version trimmed
   */
  public static BytesValue trim(final BytesValue source) {
    return BytesValue.of(trim(source.getValue()));
  }

  /**
   * Trim version of the {@code source}.
   *
   * @param source byte array which may contain version.
   * @return byte array with version trimmed
   */
  public static byte[] trim(final byte[] source) {
    if (null == source || 0 == source.length) {
      return new byte[0];
    }
    return Arrays.copyOfRange(source, 1, source.length);
  }

}
