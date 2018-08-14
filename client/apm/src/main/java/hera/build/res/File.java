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

  public BufferedReader open() throws IOException {
    return newBufferedReader(getPath());
  }
}
