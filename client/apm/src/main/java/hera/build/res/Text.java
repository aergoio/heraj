/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import hera.util.DangerousConsumer;
import hera.util.DangerousSupplier;
import hera.util.IoUtils;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Text {

  protected final DangerousSupplier<InputStream> textSupplier;

  public byte[] getBytes() throws Exception {
    return IoUtils.from(textSupplier.get());
  }

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
}
