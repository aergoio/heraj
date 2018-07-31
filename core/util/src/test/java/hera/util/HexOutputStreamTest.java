/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class HexOutputStreamTest extends AbstractTestCase {
  @Test
  public void testWrite() throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    final HexOutputStream hexOut = new HexOutputStream(byteOut);
    hexOut.write('A');
    assertEquals('A', Integer.parseInt(new String(byteOut.toByteArray()), 16));
  }
}