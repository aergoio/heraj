/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.res;

import static java.nio.file.Files.newBufferedReader;

import hera.build.Resource;
import java.io.BufferedReader;
import java.io.IOException;

public class File extends Resource {

  public File(final Project project, final String path) {
    super(project, path);
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
      return newBufferedReader(getPath());
    } catch (final IOException ex) {
      logger.error("Fail to open {}", getPath());
      throw ex;
    }
  }
}
