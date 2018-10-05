/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class Sha256Utils {
  /**
   * Convert str to byte array.
   *
   * @param str input string
   *
   * @return converted byte array
   */
  public static byte[] getBytes(CharSequence str) {
    try {
      return str.toString().getBytes("utf8");
    } catch (final UnsupportedEncodingException ex) {
      throw new IllegalStateException(ex);
    }
  }


  /**
   * Digest raw string.
   *
   * <p>
   * from "hello, world" to 09CA7E4EAA6E8AE9C7D261167129184883644D07DFBA7CBFBC4C8A2E08360D5B
   * </p>
   *
   * @param raw raw byte array to encode
   *
   * @return encoded byte array
   */
  public static byte[] digest(final byte[] raw) {
    try {
      final MessageDigest digest = createDigest();
      return digest.digest(raw);
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Digest raw string.
   *
   * <p>
   * from "hello, " + "world" to 09CA7E4EAA6E8AE9C7D261167129184883644D07DFBA7CBFBC4C8A2E08360D5B
   * </p>
   *
   * @param raws raw byte arrays to encode
   *
   * @return encoded byte array
   */
  public static byte[] digest(final byte[]... raws) {
    try {
      final MessageDigest digest = createDigest();
      for (final byte[] raw : raws) {
        digest.update(raw);
      }
      return digest.digest();
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  protected static MessageDigest createDigest() throws NoSuchAlgorithmException {
    return MessageDigest.getInstance("SHA-256");
  }

  /**
   * Mask string with '*'.
   *
   * @param raw input string
   * @return string to be masked
   */
  public static String mask(final CharSequence raw) {
    if (null == raw) {
      return null;
    }
    final StringBuilder buffer = new StringBuilder(raw.length());
    IntStream.range(0, raw.length()).forEach(i -> buffer.append("*"));
    return buffer.toString();
  }
}
