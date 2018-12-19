/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.NumberUtils.byteArrayToPostive;
import static hera.util.NumberUtils.postiveToByteArray;

import com.google.protobuf.ByteString;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.exception.RpcArgumentException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TransportUtils {

  /**
   * Transform {@link BytesValue} to {@link ByteString} in protobuf. If either bytesValue or value
   * of bytesValue is null, return {@link ByteString#EMPTY}
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
   * Transform {@link BytesValue} to {@link ByteString} in protobuf. If aer isn null, return
   * {@link ByteString#EMPTY}.
   *
   * @param aer an aer
   * @return protobuf {@link ByteString}
   */
  public static ByteString copyFrom(final Aer aer) {
    if (null == aer) {
      return ByteString.EMPTY;
    }
    return ByteString.copyFrom(postiveToByteArray(aer.getValue()));
  }

  /**
   * Parse raw aer to {@link Aer}.
   *
   * @param rawAer a raw aer
   * @return parsed {@link Aer}.
   */
  public static Aer parseToAer(final ByteString rawAer) {
    if (null == rawAer || ByteString.EMPTY.equals(rawAer)) {
      return null;
    }
    return Aer.of(byteArrayToPostive(rawAer.toByteArray()));
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

  /**
   * Check a condition and throws {@link RpcArgumentException} if condition isn't fulfilled.
   *
   * @param condition condition to check
   * @param target argument target name
   * @param requirement argument requirement
   * @throws RpcArgumentException if condition is false
   */
  public static void assertArgument(final boolean condition, final String target,
      final String requirement) {
    if (false == condition) {
      throw new RpcArgumentException(target, requirement);
    }
  }

}
