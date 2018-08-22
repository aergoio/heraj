/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static com.google.common.io.MoreFiles.createParentDirectories;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hera.util.IoUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

public class FileSet {

  /**
   * Build from filesystem.
   *
   * @param path base location
   *
   * @return built fileset
   *
   * @throws IOException Fail to access location or sub location
   */
  public static FileSet from(final Path path) throws IOException {
    final FileSet fileSet = new FileSet();
    rake(path, fileSet, path);
    return fileSet;
  }

  /**
   * Rake fileset from filesystem.
   *
   * @param base base location
   * @param fileSet collected fileset
   * @param path current location
   *
   * @throws IOException Fail to access location or sub location
   */
  public static void rake(final Path base, final FileSet fileSet, final Path path)
      throws IOException {
    if (!Files.exists(path)) {
      return;
    }
    if (Files.isDirectory(path)) {
      for (final Path p : Files.list(path).collect(toList())) {
        rake(base, fileSet, p);
      }
    } else {
      final FileContent fileContent = new FileContent(
          base.relativize(path).toString(),
          () -> Files.newInputStream(path)
      );
      fileSet.add(fileContent);
    }
  }

  @JsonIgnore
  protected final transient Logger logger = getLogger(getClass());

  @Getter
  @Setter
  protected Set<FileContent> fileSet;

  public FileSet() {
    this(new TreeSet<>());
  }

  public FileSet(final Collection<FileContent> fileSet) {
    this.fileSet = new TreeSet<>(fileSet);
  }

  public void add(final FileContent file) {
    ofNullable(file).ifPresent(fileSet::add);
  }

  public Stream<FileContent> stream() {
    return fileSet.stream();
  }

  public void addAll(final FileSet that) {
    this.fileSet.addAll(that.fileSet);
  }

  /**
   * Copy files to {@code base}.
   *
   * @param base target location
   *
   * @throws IOException On failure to write files.
   */
  public void copyTo(final Path base) throws IOException {
    logger.trace("Copying to {}...", base);
    Files.createDirectories(base);

    logger.debug("Files: {}", fileSet);
    for (final FileContent fileContent: fileSet) {
      final Path filePath = base.resolve(fileContent.getPath());
      createParentDirectories(filePath);
      try (final InputStream in = fileContent.open()) {
        Files.write(filePath, IoUtils.from(in));
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
