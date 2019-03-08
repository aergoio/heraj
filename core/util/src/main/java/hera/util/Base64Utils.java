/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;

/**
 * Base58 utils class. Encoding, decoding logic is copied from bitcoinj.
 */
public class Base64Utils {

  protected static final Logger logger = getLogger(Base64Utils.class);

  /**
   * Encode raw byte array to base.
   *
   * @param raw a raw byte array to encode
   * @return base64 encoded string
   */
  public static String encode(final byte[] raw) {
    if (null == raw || raw.length == 0) {
      return StringUtils.EMPTY_STRING;
    }
    return BaseEncoding.base64().encode(raw);
  }

  /**
   * Decode base64 encoded string to raw byte array.
   *
   * @param base64Encoded base64 encoded string
   * @return a decoded raw byte array
   */
  public static byte[] decode(final String base64Encoded) {
    try {
      if (null == base64Encoded || base64Encoded.isEmpty()) {
        return new byte[0];
      }
      return BaseEncoding.base64().decode(base64Encoded);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

}
