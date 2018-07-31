/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static com.google.common.io.Closeables.close;
import static com.google.common.io.Closeables.closeQuietly;
import static hera.util.HexUtils.dump;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class HexUtilsTest extends AbstractTestCase {

  private String[] testcase;

  /**
   * set up.
   */
  @Before
  public void setUp() {
    testcase = new String[100];

    for (int i = 0; i < testcase.length; ++i) {
      StringBuilder sb = new StringBuilder(64);
      Random random = new Random();
      for (int j = 0; j < 64; ++j) {
        char c = HexUtils.HEXA_CHARS[random.nextInt(HexUtils.HEXA_CHARS.length)];
        sb.append(c);
      }
      testcase[i] = sb.toString();
    }
  }

  @Test
  public void testDump() throws Exception {

    byte[] bytes = new byte[1024];

    assertTrue(bytes.length < HexUtils.dump(bytes).length());

    for (int i = 0; i < testcase.length; i++) {
      final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      final StringReader reader = new StringReader(testcase[i]);
      try {
        assertTrue(0 == (testcase[i].length() % 2));
        char[] characters = new char[2];
        while (2 == reader.read(characters)) {
          byteOut.write((byte) Integer.parseInt(new String(characters), 16));
        }
        assertNotNull(dump(byteOut.toByteArray()));
      } finally {
        close(byteOut, true);
        closeQuietly(reader);
      }
    }
  }
}
