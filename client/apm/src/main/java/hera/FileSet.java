/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.IoUtils.from;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class FileSet {
  protected Set<FileContent> fileSet = new TreeSet<>();

  public void add(final FileContent file) {
    ofNullable(file).ifPresent(fileSet::add);
  }

  public Stream<FileContent> stream() {
    return fileSet.stream();
  }

  /**
   * Copy files to {@code base}.
   *
   * @param base target location
   *
   * @throws IOException On failure to write files.
   */
  public void copyTo(final Path base) throws IOException {
    Files.createDirectories(base);

    for (final FileContent fileContent: fileSet) {
      final Path filePath = base.resolve(fileContent.getPath());
      Files.createDirectory(filePath.getParent());
      try (final InputStream in = fileContent.open()) {
        Files.write(filePath, from(in));
      } catch (final IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  @Override
  public String toString() {
    return fileSet.size() + " file(s)";
  }
}
