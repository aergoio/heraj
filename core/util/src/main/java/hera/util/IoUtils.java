/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.slf4j.Logger;

public class IoUtils {
  protected static final Logger logger = getLogger(IoUtils.class);

  /**
   * Call #close if possible.
   * <p>
   *   Call #close if the element of {@code closeables} is next:
   * </p>
   * <ul>
   *   <li>{@link Closeable}</li>
   *   <li>{@link AutoCloseable}</li>
   * </ul>
   * <p>
   *   Ignore on exception
   * </p>
   *
   * @param closeables object instances to close
   */
  public static void tryClose(final Object... closeables) {
    for (final Object obj : closeables) {
      try {
        if (obj instanceof Closeable) {
          final Closeable closeable = (Closeable) obj;
          closeable.close();
        } else if (obj instanceof AutoCloseable) {
          final AutoCloseable closeable = (AutoCloseable) obj;
          closeable.close();
        }
      } catch (final Throwable th) {
        logger.trace("Ignore exception: {}", th);
      }
    }
  }

  /**
   * Call #flush if possible.
   * <p>
   *   Call #flush if the element of {@code flushables} is next:
   * </p>
   * <ul>
   *   <li>{@link Flushable}</li>
   * </ul>
   *
   * @param flushable object instances to flush
   */
  public static void tryFlush(final Object... flushable) {
    for (final Object obj : flushable) {
      try {
        if (obj instanceof Flushable) {
          ((Flushable) obj).flush();
        }
      } catch (final Throwable th) {
        logger.trace("Ignore exception: {}", th);
      }
    }
  }

  /**
   * Process streaming.
   *
   * @param in        {@link InputStream}
   * @param consumer  instance to use streaming
   *
   * @return read bytes
   *
   * @throws IOException If fail to read or process
   */
  public static int stream(final InputStream in, final StreamConsumer consumer) throws Exception {
    assertNotNull(in);

    final byte[] buffer = new byte[1024];
    int readBytes;
    int sum = 0;
    while (0 < (readBytes = in.read(buffer))) {
      consumer.apply(buffer, 0, readBytes);
      sum += readBytes;
    }

    return sum;
  }

  /**
   * Redirect {@code from} to {@code to}.
   *
   * @param from {@link InputStream} to read
   * @param to   {@link OutputStream} to write
   *
   * @return the number of bytes to redirect
   *
   * @throws IOException Fail to read or write
   */
  public static int redirect(final InputStream from, final OutputStream to) throws IOException {
    assertNotNull(to);
    try {
      return stream(from, to::write);
    } catch (final Exception e) {
      throw (IOException) e;
    }
  }

  /**
   * Redirect {@code from} to {@code to}.
   *
   * @param from {@link Reader} to read
   * @param to {@link Writer} to write
   *
   * @return the number of bytes to redirect
   *
   * @throws IOException Fail to read or write
   */
  public static int redirect(final Reader from, final Writer to) throws IOException {
    assertNotNull(from);
    assertNotNull(to);

    final char[] buffer = new char[1024];
    int readBytes;
    int sum = 0;
    while (0 < (readBytes = from.read(buffer))) {
      to.write(buffer, 0, readBytes);
      sum += readBytes;
    }

    return sum;
  }

  /**
   * Read from {@code in} and return all bytes.
   *
   * @param in {@link InputStream} to read
   *
   * @return read bytes
   *
   * @throws IOException Fail to read
   */
  public static byte[] from(
      final InputStream in)
      throws IOException {
    final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

    final byte[] bytes = new byte[10000];
    int readBytes;
    while (0 < (readBytes = in.read(bytes))) {
      byteOut.write(bytes, 0, readBytes);
    }
    return byteOut.toByteArray();
  }

  /**
   * Read from {@code reader} and return string.
   *
   * @param reader {@link Reader} to read
   * @return read string
   *
   * @throws IOException Fail to read
   */
  public static String from(
      final Reader reader)
      throws IOException {
    final StringWriter writer = new StringWriter();

    final char[] bytes = new char[10000];
    int readBytes;
    while (0 < (readBytes = reader.read(bytes))) {
      writer.write(bytes, 0, readBytes);
    }
    return writer.toString();
  }

  /**
   * Calculate checksum.
   *
   * @param   in {@link InputStream} containing content
   * @return  checksum
   * @throws  IOException              Fail to read content
   * @throws  NoSuchAlgorithmException No MD5 algorithm
   */
  public static byte[] getChecksum(final InputStream in)
      throws IOException, NoSuchAlgorithmException {
    final MessageDigest checksumGenerator = MessageDigest.getInstance("MD5");

    final DigestInputStream dis = new DigestInputStream(in, checksumGenerator);
    int readBytes = redirect(dis, new ByteArrayOutputStream());
    logger.debug("{} byte(s) read", readBytes);
    return checksumGenerator.digest();
  }

  /**
   * Calculate checksum as string.
   *
   * @param   in {@link InputStream} containing content
   * @return  checksum
   * @throws  IOException              Fail to read content
   * @throws  NoSuchAlgorithmException No MD5 algorithm
   */
  public static String getChecksumAsString(final InputStream in)
      throws IOException, NoSuchAlgorithmException {
    final byte[] bytes = getChecksum(in);
    return Base64.getEncoder().encodeToString(bytes);
  }
}
