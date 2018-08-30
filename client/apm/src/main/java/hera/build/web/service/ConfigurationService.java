/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static java.nio.file.Files.newInputStream;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import hera.ProjectFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigurationService extends AbstractService {

  /**
   * Project file path.
   */
  protected final Path projectFilePath;

  /**
   * Provide project file.
   *
   * @return project file
   */
  public Optional<ProjectFile> getProjectFile() {
    logger.debug("Project file path: {}", projectFilePath);
    try (final InputStream in = newInputStream(projectFilePath)) {
      return ofNullable(ProjectFile.from(in));
    } catch (final IOException ex) {
      return empty();
    }
  }
}
