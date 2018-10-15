/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import org.junit.Test;


public class IoUtilsTest {

  @Test
  public void testTryClose() {
    final Object[][] testParameters = new Object[][] {{new Object[] {new TestCloseable()}, true},
        {new Object[] {new TestAutoCloseable()}, true},
        {new Object[] {new TestCloseable(), new TestCloseable()}, true},
        {new Object[] {new TestCloseable(), new TestAutoCloseable()}, true},
        {new Object[] {new TestAutoCloseable(), new TestAutoCloseable()}, true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[1];
      Object[] closeables = (Object[]) testParameter[0];

      for (int i = 0; i < closeables.length; ++i) {
        if (closeables[i] instanceof Closeable) {
          TestCloseable testCloseable = (TestCloseable) closeables[i];
          assertFalse(testCloseable.isClosed);
        } else if (closeables[i] instanceof AutoCloseable) {
          TestAutoCloseable testAutoCloseable = (TestAutoCloseable) closeables[i];
          assertFalse(testAutoCloseable.isClosed);
        }
      }

      IoUtils.tryClose(closeables);

      for (int i = 0; i < closeables.length; ++i) {
        if (closeables[i] instanceof Closeable) {
          TestCloseable testCloseable = (TestCloseable) closeables[i];
          assertEquals(expected, testCloseable.isClosed);
        } else if (closeables[i] instanceof AutoCloseable) {
          TestAutoCloseable testAutoCloseable = (TestAutoCloseable) closeables[i];
          assertEquals(expected, testAutoCloseable.isClosed);
        }
      }
    }
  }

  class TestCloseable implements Closeable {
    private boolean isClosed = false;

    @Override
    public void close() throws IOException {
      isClosed = true;
    }
  }

  class TestAutoCloseable implements AutoCloseable {
    private boolean isClosed = false;

    @Override
    public void close() throws Exception {
      isClosed = true;
    }
  }

  @Test
  public void testTryFlush() {
    final Object[][] testParameters = new Object[][] {{new Object[] {new TestFlushable()}, true},
        {new Object[] {new TestFlushable(), new TestFlushable()}, true},
        {new Object[] {new TestFlushable(), new TestFlushable(), new TestFlushable()}, true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[1];
      Object[] flushable = (Object[]) testParameter[0];

      for (int i = 0; i < flushable.length; ++i) {
        if (flushable[i] instanceof Flushable) {
          TestFlushable testFlushable = (TestFlushable) flushable[i];
          assertFalse(testFlushable.isFlushed);
        }
      }

      IoUtils.tryFlush(flushable);

      for (int i = 0; i < flushable.length; ++i) {
        if (flushable[i] instanceof Flushable) {
          TestFlushable testFlushable = (TestFlushable) flushable[i];
          assertEquals(expected, testFlushable.isFlushed);
        }
      }
    }
  }

  class TestFlushable implements Flushable {
    private boolean isFlushed = false;

    @Override
    public void flush() throws IOException {
      isFlushed = true;
    }
  }

  @Test
  public void testRedirectStream() throws IOException {
    final Object[][] testParameters = new Object[][] {
        {new ByteArrayInputStream(new byte[] {1, 2, 3}), new ByteArrayOutputStream(), 3},
        {new ByteArrayInputStream(new byte[] {4, 5, 6, 7, 8}), new ByteArrayOutputStream(), 5},
        {new ByteArrayInputStream(new byte[] {9, 10, 11, 12, 13, 14, 15}),
            new ByteArrayOutputStream(), 7}};

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[2];
      InputStream from = (InputStream) testParameter[0];
      OutputStream to = (OutputStream) testParameter[1];
      assertEquals(expected, IoUtils.redirect(from, to));
    }
  }

  @Test
  public void testRedirectReaderWriter() throws IOException {
    final Object[][] testParameters = new Object[][] {
        {new ByteArrayInputStream(new byte[] {1, 2, 3}), new ByteArrayOutputStream(), 3},
        {new ByteArrayInputStream(new byte[] {4, 5, 6, 7, 8}), new ByteArrayOutputStream(), 5},
        {new ByteArrayInputStream(new byte[] {9, 10, 11, 12, 13, 14, 15}),
            new ByteArrayOutputStream(), 7}};

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[2];
      InputStream inputStream = (InputStream) testParameter[0];
      OutputStream outputStream = (OutputStream) testParameter[1];

      Reader from = new BufferedReader(new InputStreamReader(inputStream));
      Writer to = new BufferedWriter(new OutputStreamWriter(outputStream));
      assertEquals(expected, IoUtils.redirect(from, to));
    }
  }

  @Test
  public void testFromOnByte() throws IOException {
    final Object[][] testParameters =
        new Object[][] {{new ByteArrayInputStream(new byte[] {1, 2, 3}), new byte[] {1, 2, 3}},
            {new ByteArrayInputStream(new byte[] {4, 5, 6, 7, 8}), new byte[] {4, 5, 6, 7, 8}},
            {new ByteArrayInputStream(new byte[] {9, 10, 11, 12, 13, 14, 15}),
                new byte[] {9, 10, 11, 12, 13, 14, 15}}};

    for (final Object[] testParameter : testParameters) {
      byte[] expected = (byte[]) testParameter[1];
      InputStream in = (InputStream) testParameter[0];

      byte[] actual = IoUtils.from(in);
      for (int i = 0; i < actual.length; ++i) {
        assertEquals(expected[i], actual[i]);
      }
    }
  }

  @Test
  public void testFromOnString() throws IOException {
    final Object[][] testParameters =
        new Object[][] {{new CharArrayReader(new char[] {'a', 'b', 'c'}), "abc"},
            {new StringReader("hello"), "hello"}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      Reader reader = (Reader) testParameter[0];
      assertEquals(expected, IoUtils.from(reader));
    }
  }

  @Test
  public void testGetChecksum() throws NoSuchAlgorithmException, IOException {
    final Object[][] testParameters =
        new Object[][] {
            {new ByteArrayInputStream(new byte[] {1, 2, 3}),
                new byte[] {82, -119, -33, 115, 125, -11, 115, 38, -4, -35, 34, 89, 122, -5, 31,
                    -84}},
            {new ByteArrayInputStream(new byte[] {4, 5, 6, 7, 8}),
                new byte[] {-94, -23, -23, -60, 5, -4, -76, 40, 8, -49, -96, -22, -14, 82, 124,
                    55}},
            {new ByteArrayInputStream(new byte[] {9, 10, 11, 12, 13, 14, 15}), new byte[] {-80, -53,
                24, -101, -113, 102, 100, 106, -112, 42, 57, -61, 15, 35, -61, -93}}};

    for (final Object[] testParameter : testParameters) {
      byte[] expected = (byte[]) testParameter[1];
      InputStream in = (InputStream) testParameter[0];

      byte[] actual = IoUtils.getChecksum(in);
      for (int i = 0; i < actual.length; ++i) {
        assertEquals(expected[i], actual[i]);
      }
    }
  }

  @Test
  public void testGetChecksumAsString() throws NoSuchAlgorithmException, IOException {
    final Object[][] testParameters = new Object[][] {
        {new ByteArrayInputStream(new byte[] {1, 2, 3}), "Uonfc331cyb83SJZevsfrA=="},
        {new ByteArrayInputStream(new byte[] {4, 5, 6, 7, 8}), "ounpxAX8tCgIz6Dq8lJ8Nw=="},
        {new ByteArrayInputStream(new byte[] {9, 10, 11, 12, 13, 14, 15}),
            "sMsYm49mZGqQKjnDDyPDow=="}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      InputStream in = (InputStream) testParameter[0];
      assertEquals(expected, IoUtils.getChecksumAsString(in));
    }
  }
}
