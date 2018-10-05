/*
 * @copyright defined in LICENSE.txt
 */

package hera;

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
  public static void validate(final byte[] source, final byte version) {
    if (null == source) {
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
  public static byte[] envelop(final byte[] source, final byte version) {
    if (null == source) {
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
   * @param versionEnveloped byte array which may contain version.
   * @return byte array with version trimmed
   */
  public static byte[] trim(final byte[] versionEnveloped) {
    if (versionEnveloped.length == 0) {
      return new byte[0];
    }
    return Arrays.copyOfRange(versionEnveloped, 1, versionEnveloped.length);
  }

}
