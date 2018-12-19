/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.NumberUtils.postiveToByteArray;
import static hera.util.TransportUtils.assertArgument;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.inputStreamToByteArray;
import static hera.util.TransportUtils.longToByteArray;
import static hera.util.TransportUtils.parseToAer;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.exception.RpcArgumentException;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import org.junit.Test;

public class TransportUtilsTest extends AbstractTestCase {

  @Test
  public void testCopyFromWithBytesValue() {
    final BytesValue filledValue = BytesValue.of(randomUUID().toString().getBytes());
    final BytesValue emptyValue = BytesValue.EMPTY;;
    final BytesValue nullValue = null;
    assertEquals(ByteString.copyFrom(filledValue.getValue()), copyFrom(filledValue));
    assertEquals(ByteString.EMPTY, copyFrom(emptyValue));
    assertEquals(ByteString.EMPTY, copyFrom(nullValue));
  }

  @Test
  public void testCopyFromWithAer() {
    final Aer filledValue = Aer.of("100", Unit.AERGO);
    final Aer nullValue = null;
    assertEquals(ByteString.copyFrom(postiveToByteArray(filledValue.getValue())),
        copyFrom(filledValue));
    assertEquals(ByteString.EMPTY, copyFrom(nullValue));
  }

  @Test
  public void testParseToAer() {
    final Aer expected = Aer.of("100", Unit.GAER);
    final ByteString rawAer = copyFrom(expected);
    assertEquals(expected, parseToAer(rawAer));
    assertEquals(null, parseToAer(null));
  }

  @Test
  public void testLongToByteArray() {
    byte[] expected = {0x7f, 0x6f, 0x5f, 0x4f, 0x3f, 0x2f, 0x1f, 0x0f};
    byte[] actual = longToByteArray(Long.decode("0x0f1f2f3f4f5f6f7f"));
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testInputStreamToByteArray() {
    byte[] expected = randomUUID().toString().getBytes();
    byte[] actual = inputStreamToByteArray(new ByteArrayInputStream(expected));
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testAssertArgument() {
    assertArgument(true, "object", "requirement");

    try {
      assertArgument(false, "object", "requirement");
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }
  }

}
