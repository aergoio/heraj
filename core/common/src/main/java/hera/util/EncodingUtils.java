/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import hera.api.model.BytesValue;
import hera.exception.DecodingFailureException;
import hera.exception.DecodingFailureException.Format;

public class EncodingUtils {

  /**
   * Encode bytesValue to base58.
   *
   * @param bytesValue a bytesValue
   * @return base58 encoded string
   */
  public static String encodeBase58(final BytesValue bytesValue) {
    return (null != bytesValue && !bytesValue.isEmpty()) ? Base58Utils.encode(bytesValue.getValue())
        : "";
  }

  /**
   * Decode base58 encoded string.
   *
   * @param encoded base58 encoded string
   * @return decoded string
   * @throws DecodingFailureException if decoding failure
   */
  public static BytesValue decodeBase58(final String encoded) {
    try {
      return null != encoded ? new BytesValue(Base58Utils.decode(encoded)) : BytesValue.EMPTY;
    } catch (Exception e) {
      throw new DecodingFailureException(encoded, Format.Base58WithCheck);
    }
  }

  /**
   * Encode bytesValue to base58 with checksum.
   *
   * @param bytesValue a bytesValue
   * @return base58 with checksum encoded string
   */
  public static String encodeBase58WithCheck(final BytesValue bytesValue) {
    return (null != bytesValue && !bytesValue.isEmpty())
        ? Base58Utils.encodeWithCheck(bytesValue.getValue())
        : "";
  }

  /**
   * Decode base58 with checksum encoded string.
   *
   * @param encoded base58 with checksum encoded string
   * @return decoded string
   * @throws DecodingFailureException if decoding failure
   */
  public static BytesValue decodeBase58WithCheck(final String encoded) {
    try {
      return null != encoded ? new BytesValue(Base58Utils.decodeWithCheck(encoded))
          : BytesValue.EMPTY;
    } catch (Exception e) {
      throw new DecodingFailureException(encoded, Format.Base58WithCheck);
    }
  }

}
