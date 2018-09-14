/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import hera.util.DangerousConsumer;
import hera.util.DangerousSupplier;
import hera.util.IoUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Text {

  protected final DangerousSupplier<InputStream> textSupplier;

  /**
   * Read using {@link DangerousSupplier}.
   *
   * @param supplier {@link OutputStream} supplier
   *
   * @throws Exception Fail to read or write
   */
  public void read(final DangerousSupplier<OutputStream> supplier) throws Exception {
    try (final InputStream in = textSupplier.get()) {
      IoUtils.redirect(in, supplier.get());
    }
  }

  /**
   * Read using {@link DangerousConsumer}.
   *
   * @param consumer {@link InputStream} consumer
   *
   * @throws Exception Fail to read
   */
  public void read(final DangerousConsumer<InputStream> consumer) throws Exception {
    try (final InputStream in = textSupplier.get()) {
      consumer.accept(in);
    }
  }

  /**
   * Get content text as String.
   *
   * @return content
   *
   * @throws Exception Fail to get content
   */
  public String get() throws Exception {
    final InputStream in = textSupplier.get();
    try (final Reader reader = new InputStreamReader(in)) {
      return IoUtils.from(reader);
    }
  }
}
