/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import org.junit.Test;

public class TransportUtilsTest extends AbstractTestCase {

  @Test
  public void testCopyFrom() {
    BytesValue bytesValue = BytesValue.of(randomUUID().toString().getBytes());
    ByteString result = copyFrom(bytesValue);
    assertNotNull(result);
  }

  @Test
  public void testCopyFromWithNullBytesValue() {
    BytesValue bytesValue = BytesValue.of(null);
    ByteString result = copyFrom(bytesValue);
    assertNull(result);
  }

  @Test
  public void testCopyFromWithNullInput() {
    ByteString result = copyFrom(null);
    assertNull(result);
  }

}
