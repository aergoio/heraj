/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static com.google.common.io.Closeables.close;
import static hera.util.ValidationUtils.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class HexUtils {
  /* Dump Format */
  protected static final char CONTROL_CHARS_SHOWER = '.';

  protected static final char[] HEXA_CHARS =
      new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  protected static final int N_INT_BY_BYTE = 4;

  protected static final int WIDTH_PER_LINE = 32;

  protected static final char TWO_BYTES_CHARS_SHOWER = '?';

  /**
   * Append hex value of {@code ch} to {@code buffer}.
   *
   * @param buffer  buffer to append to
   * @param ch      value to append
   */
  public static void appendHexa(final StringBuilder buffer, final int ch) {
    if (ch < 16) {
      buffer.append('0');
      buffer.append(HEXA_CHARS[(0x0f & (ch))]);
    } else {
      buffer.append(HEXA_CHARS[(0x0f & (ch >> 4))]);
      buffer.append(HEXA_CHARS[(0x0f & (ch))]);
    }
  }

  /**
   * Append hex values of {@code bytes} to {@code buffer}.
   *
   * @param buffer  buffer to append to
   * @param bytes   values to append
   */
  public static void appendHexa(final StringBuilder buffer, final byte[] bytes) {
    try {
      final ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
      int ch = 0;
      while (0 <= (ch = byteIn.read())) {
        appendHexa(buffer, ch);
      }
    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Encode byte array to hexa.
   * 
   * @param bytes byte array to encode
   * 
   * @return encoded string
   */
  public static String encode(final byte[] bytes) {
    final StringBuilder buffer = new StringBuilder();
    appendHexa(buffer, bytes);
    return buffer.toString();
  }

  protected static  int convert(final int ch) {
    if ('0' <= ch && ch <= '9') {
      return ch - '0';
    } else if ('A' <= ch && ch <= 'F') {
      return 10 + ch - 'A';
    } else if ('a' <= ch && ch <= 'f') {
      return 10 + ch - 'a';
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Decode hex string to byte array.
   *
   * @param str hex string
   *
   * @return decoded byte array
   */
  public static byte[] decode(final String str) {
    final StringReader reader = new StringReader(str);
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    int ch1 = 0;
    try {
      while (0 < (ch1 = reader.read())) {
        int ch2 = reader.read();
        assertTrue(0 <= ch2);
        byteOut.write(convert(ch1) << 4 | convert(ch2));
      }
      return byteOut.toByteArray();
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  protected static void lineEnd(final StringBuilder hexPart, final StringBuilder textPart,
      final StringBuilder ret) {
    hexPart.append("     |");

    textPart.append("|\n");

    ret.append(hexPart);
    ret.append(textPart);

    hexPart.delete(0, hexPart.capacity());
    textPart.delete(0, textPart.capacity());
  }

  /**
   * Convert {@code data} to readable dump for human.
   *
   * @param data byte array to convert
   *
   * @return converted string
   */
  public static String dump(final byte[] data) {
    if (null == data) {
      return "<<null>>";
    }
    return dump(data, 0, data.length);
  }

  /**
   * Convert {@code data}'s subsequence to readable dump for human.
   *
   * @param data    byte array to convert
   * @param offset  sequence start index
   * @param length  sequence length
   *
   * @return converted string
   */
  public static String dump(final byte[] data, final int offset, final int length) {
    final StringWriter writer = new StringWriter();
    dump(data, offset, length, writer);
    return writer.toString();
  }

  /**
   * Convert {@code data}'s subsequence to readable dump for human and write to {@code writer}.
   *
   * @param data    byte array to convert
   * @param offset  sequence start index
   * @param length  sequence length
   * @param writer  writer to write result
   */
  public static void dump(final byte[] data, final int offset, final int length,
      final Writer writer) {

    try {
      if (null == data) {
        writer.write("<<null>>");
        return;
      }
      if (data.length <= 0) {
        writer.write("<<EMPTY BYTES>>");
        return;
      }

      final ByteArrayInputStream reader = new ByteArrayInputStream(data, offset, length);
      final StringBuilder ret = new StringBuilder();
      final StringBuilder hexPart = new StringBuilder();
      final StringBuilder textPart = new StringBuilder();

      int address = 0;
      int cnt = 0;

      hexPart.append("          ");

      for (int i = 0, n = WIDTH_PER_LINE / 4; i < n; i++) {
        hexPart.append("+-------");
        textPart.append("+---");
      }

      lineEnd(hexPart, textPart, ret);

      int ch;
      while (0 <= (ch = reader.read())) {
        if (0 == cnt) {
          for (int i = N_INT_BY_BYTE - 1; i >= 0; i--) {
            final int printByte = 0xFF & (address >> (8 * i));
            appendHexa(hexPart, printByte);
          }
          hexPart.append("  ");
          address += WIDTH_PER_LINE;
        }

        appendHexa(hexPart, ch);
        if (ch < 32 || 127 <= ch) {
          textPart.append(CONTROL_CHARS_SHOWER);
        } else {
          textPart.append((char) ch);
        }
        cnt++;

        if (WIDTH_PER_LINE == cnt) {
          lineEnd(hexPart, textPart, ret);
          cnt = 0;
        }
      } // END of while ( 0 <= (ch = reader.read() ) )

      if (0 != cnt) {
        for (; cnt < WIDTH_PER_LINE; ++cnt) {
          hexPart.append("  ");
          textPart.append(' ');
        }
        lineEnd(hexPart, textPart, ret);
      }

      writer.write(ret.toString());
      close(writer, true);
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
