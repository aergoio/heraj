/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.NumberUtils.positiveToByteArray;
import static hera.util.TransportUtils.assertArgument;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.parseToAer;
import static hera.util.TransportUtils.sha256AndEncodeHexa;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.exception.RpcArgumentException;
import org.junit.Test;

public class TransportUtilsTest extends AbstractTestCase {

  @Test
  public void testCopyFromWithRawBytes() {
    final byte[] filledValue = randomUUID().toString().getBytes();
    final byte[] emptyValue = new byte[0];
    final byte[] nullValue = null;
    assertEquals(ByteString.copyFrom(filledValue), copyFrom(filledValue));
    assertEquals(ByteString.EMPTY, copyFrom(emptyValue));
    assertEquals(ByteString.EMPTY, copyFrom(nullValue));
  }

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
    assertEquals(ByteString.copyFrom(positiveToByteArray(filledValue.getValue())),
        copyFrom(filledValue));
    assertEquals(ByteString.EMPTY, copyFrom(nullValue));
  }

  @Test
  public void testCopyFromWithLong() {
    final ByteString expected =
        ByteString.copyFrom(new byte[] {0x7f, 0x6f, 0x5f, 0x4f, 0x3f, 0x2f, 0x1f, 0x0f});
    ByteString actual = copyFrom(Long.decode("0x0f1f2f3f4f5f6f7f").longValue());
    assertEquals(expected, actual);
  }

  @Test
  public void testParseToAerWithRawBytes() {
    final Aer expected = Aer.of("100", Unit.GAER);
    final byte[] rawAer = copyFrom(expected).toByteArray();
    assertEquals(expected, parseToAer(rawAer));
  }

  @Test
  public void testParseToAerWithByteString() {
    final Aer expected = Aer.of("100", Unit.GAER);
    final ByteString rawAer = copyFrom(expected);
    assertEquals(expected, parseToAer(rawAer));
  }

  @Test
  public void testSha256AndEncodeHexa() {
    final String string = randomUUID().toString();
    final String expected = HexUtils.encode(Sha256Utils.digest(string.getBytes()));
    final String actual = sha256AndEncodeHexa(string);
    assertEquals(expected, actual);
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
