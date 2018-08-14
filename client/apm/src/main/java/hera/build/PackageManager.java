/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static hera.ApmConstants.MODULES_BASE;
import static hera.ApmConstants.PROJECT_FILENAME;
import static hera.util.FilepathUtils.append;
import static hera.util.StringUtils.nvl;

import hera.ProjectFile;
import hera.build.res.Project;
import hera.exception.PackageNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PackageManager {

  protected final String repostiroyLocation;

  public PackageManager() {
    this(append(nvl(System.getProperty("user.home"), System.getenv("HOME")),MODULES_BASE));
  }

  /**
   * Find {@link ResourceManager} with package name.
   *
   * @param packageName package's name
   *
   * @return resource manager
   */
  public ResourceManager find(final String packageName) {
    try {
      final String packageLocation = append(repostiroyLocation, packageName);
      final String projectFileLocation = append(packageLocation, PROJECT_FILENAME);
      final Path projectFilePath = Paths.get(projectFileLocation);
      final ProjectFile projectFile = ProjectFile.from(projectFilePath);
      return new ResourceManager(new Project(packageLocation, projectFile));
    } catch (final IOException ex) {
      throw new PackageNotFoundException(ex);
    }
  }
}
