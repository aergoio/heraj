/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import static java.nio.file.Files.newBufferedReader;

import hera.build.Resource;
import hera.util.DangerousSupplier;
import hera.util.FileOpener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.Setter;

public class File extends Resource {

  @Getter
  @Setter
  protected DangerousSupplier<InputStream> contentProvider;

  public File(final Project project, final String path) {
    super(project, path);
    contentProvider = new FileOpener(getPath());
  }

  /**
   * Open file and return {@link BufferedReader}.
   *
   * @return buffered reader
   *
   * @throws IOException Fail to open
   */
  public BufferedReader open() throws IOException {
    try {
      return new BufferedReader(new InputStreamReader(contentProvider.get()));
    } catch (final IOException ex) {
      logger.error("Fail to open {}", getPath());
      throw ex;
    } catch (final Throwable ex) {
      throw new IllegalStateException(ex);
    }
  }
}
