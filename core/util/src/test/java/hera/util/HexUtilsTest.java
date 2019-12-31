/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static com.google.common.io.Closeables.close;
import static com.google.common.io.Closeables.closeQuietly;
import static hera.util.HexUtils.decode;
import static hera.util.HexUtils.dump;
import static hera.util.HexUtils.encodeLower;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
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
        char c = HexUtils.HEXA_CHARS_UPPER[random.nextInt(HexUtils.HEXA_CHARS_UPPER.length)];
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

  @Test
  public void testDecode() throws Exception {
    assertArrayEquals(
        new byte[] {
            (byte) 0xE2, (byte) 0x08, (byte) 0xDF, (byte) 0x08, (byte) 0x3F, (byte) 0x5D,
            (byte) 0x68, (byte) 0x1D,
            (byte) 0x3B, (byte) 0x89, (byte) 0x50, (byte) 0x69, (byte) 0xC6, (byte) 0xD9,
            (byte) 0x53, (byte) 0x65,
            (byte) 0x3C, (byte) 0x8E, (byte) 0x79, (byte) 0xA1, (byte) 0xCA, (byte) 0x94,
            (byte) 0xF7, (byte) 0x94,
            (byte) 0x06, (byte) 0xD1, (byte) 0x81, (byte) 0x41, (byte) 0xCE, (byte) 0x73,
            (byte) 0x95, (byte) 0x36,
            (byte) 0xab, (byte) 0xcd, (byte) 0xef
        },
        decode("E208DF083F5D681D3B895069C6D953653C8E79A1CA94F79406D18141CE739536abcdef"));

  }

  @Test
  public void testEncodeLower() throws Exception {
    assertEquals(
        "e208df083f5d681d3b895069c6d953653c8e79a1ca94f79406d18141ce739536abcdef",
        encodeLower(new byte[] {
            (byte) 0xE2, (byte) 0x08, (byte) 0xDF, (byte) 0x08, (byte) 0x3F, (byte) 0x5D,
            (byte) 0x68, (byte) 0x1D,
            (byte) 0x3B, (byte) 0x89, (byte) 0x50, (byte) 0x69, (byte) 0xC6, (byte) 0xD9,
            (byte) 0x53, (byte) 0x65,
            (byte) 0x3C, (byte) 0x8E, (byte) 0x79, (byte) 0xA1, (byte) 0xCA, (byte) 0x94,
            (byte) 0xF7, (byte) 0x94,
            (byte) 0x06, (byte) 0xD1, (byte) 0x81, (byte) 0x41, (byte) 0xCE, (byte) 0x73,
            (byte) 0x95, (byte) 0x36,
            (byte) 0xab, (byte) 0xcd, (byte) 0xef
        }));

  }

}
