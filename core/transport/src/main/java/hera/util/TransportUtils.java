/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import com.google.protobuf.ByteString;
import hera.api.model.BytesValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TransportUtils {

  /**
   * Transform {@link BytesValue} to {@link ByteString} in protobuf. If either bytesValue or value
   * of bytesValue is null, return {@code ByteString.EMPTY}
   *
   * @param bytesValue {@link BytesValue}
   * @return protobuf {@link ByteString}
   */
  public static ByteString copyFrom(final BytesValue bytesValue) {
    if (null == bytesValue || null == bytesValue.getValue()) {
      return ByteString.EMPTY;
    }
    return ByteString.copyFrom(bytesValue.getValue());
  }

  /**
   * Convert long value to byte array in a little endian.
   *
   * @param longValue long value
   * @return converted byte array in a little endian
   */
  public static byte[] longToByteArray(final long longValue) {
    final byte[] raw = new byte[8];
    raw[0] = (byte) (0xFF & (longValue));
    raw[1] = (byte) (0xFF & (longValue >> 8));
    raw[2] = (byte) (0xFF & (longValue >> 16));
    raw[3] = (byte) (0xFF & (longValue >> 24));
    raw[4] = (byte) (0xFF & (longValue >> 32));
    raw[5] = (byte) (0xFF & (longValue >> 40));
    raw[6] = (byte) (0xFF & (longValue >> 48));
    raw[7] = (byte) (0xFF & (longValue >> 56));
    return raw;
  }

  /**
   * Convert input stream to byte array.
   *
   * @param in input stream
   * @return converted byte array
   */
  public static byte[] inputStreamToByteArray(final InputStream in) {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read;
    try {
      while ((read = in.read(buffer)) != -1) {
        bos.write(buffer, 0, read);
      }
      bos.close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return bos.toByteArray();
  }

}
