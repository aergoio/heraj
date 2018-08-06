/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import com.google.protobuf.ByteString;
import hera.api.model.BytesValue;

public class TransportUtils {

  /**
   * Transform {@link BytesValue} to {@link ByteString} in protobuf. If either bytesValue or value
   * of bytesValue is null, return null.
   *
   * @param bytesValue {@link BytesValue}
   * @return protobuf {@link ByteString}
   */
  public static ByteString copyFrom(final BytesValue bytesValue) {
    if (null != bytesValue && null != bytesValue.getValue()) {
      return ByteString.copyFrom(bytesValue.getValue());
    }
    return null;
  }

}
