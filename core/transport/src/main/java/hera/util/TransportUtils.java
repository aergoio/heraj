/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import com.google.protobuf.ByteString;
import hera.api.model.BytesValue;
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

}
