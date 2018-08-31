/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import com.google.protobuf.ByteString;
import hera.api.model.BytesValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TransportUtils {

  /**
   * Transform {@link BytesValue} to {@link ByteString} in protobuf. If either bytesValue or value
   * of bytesValue is null, return {@code ByteString.EMPTY}
   *
   * @param bytesValue {@link BytesValue}
   * @return protobuf {@link ByteString}
   */
  public static ByteString copyFrom(final BytesValue bytesValue) {
    if (null != bytesValue && null != bytesValue.getValue()) {
      return ByteString.copyFrom(bytesValue.getValue());
    }
    return ByteString.EMPTY;
  }

  /**
   * Convert long value to byte array in a little endian.
   *
   * @param longValue long value
   * @return converted byte array in a little endian
   */
  public static byte[] longToByteArray(final long longValue) {
    final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    for (int i = 0; i < 8; ++i) {
      int shift = 8 * i;
      buffer.put((byte) (longValue >> shift));
    }
    return buffer.array();
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
    int read = -1;
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
