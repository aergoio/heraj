/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.longToByteArray;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import java.util.Arrays;
import org.junit.Test;

public class TransportUtilsTest extends AbstractTestCase {

  @Test
  public void testCopyFrom() {
    BytesValue bytesValue = BytesValue.of(randomUUID().toString().getBytes());
    ByteString result = copyFrom(bytesValue);
    assertTrue(!result.isEmpty());
  }

  @Test
  public void testCopyFromWithNullBytesValue() {
    BytesValue bytesValue = BytesValue.of(null);
    ByteString actualResult = copyFrom(bytesValue);
    assertEquals(ByteString.EMPTY, actualResult);
  }

  @Test
  public void testCopyFromWithNullArgument() {
    ByteString actualResult = copyFrom(null);
    assertEquals(ByteString.EMPTY, actualResult);
  }

  @Test
  public void testLongToByteArray() {
    byte[] expected = {0x7f, 0x6f, 0x5f, 0x4f, 0x3f, 0x2f, 0x1f, 0x0f};
    byte[] actual = longToByteArray(Long.decode("0x0f1f2f3f4f5f6f7f"));
    assertTrue(Arrays.equals(expected, actual));
  }

}
