/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static hera.util.ObjectUtils.equal;
import static hera.util.ObjectUtils.nvl;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.ProjectFile;
import hera.build.res.BuildResource;
import hera.build.res.PackageResource;
import hera.build.res.Project;
import hera.build.res.Source;
import hera.build.res.TestResource;
import hera.exception.PackageNotFoundException;
import hera.util.FilepathUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ResourceManager {

  protected final transient Logger logger = getLogger(getClass());

  protected final Map<String, Resource> cache = new HashMap<>();

  @Getter
  protected final Project project;

  @Getter
  @Setter
  protected PackageManager packageManager = new PackageManager();

  /**
   * Return resouce for {@code path}.
   *
   * @param path resoruce path
   *
   * @return resoruce for path
   */
  public Resource getResource(final String path) {
    logger.trace("Path: {}", path);
    final ProjectFile projectFile = project.getProjectFile();
    final String canonicalPath = FilepathUtils.getCanonicalForm(path);
    logger.trace("Canonical path: {}", canonicalPath);
    final Resource cached = cache.get(canonicalPath);
    if (null != cached) {
      return cached;
    }
    Resource created = null;
    if (equal(canonicalPath, FilepathUtils.getCanonicalForm(projectFile.getTarget()))) {
      created = new BuildResource(project, canonicalPath);
    } else {
      if (nvl(projectFile.getTests(), Collections.<String>emptyList())
          .stream().map(FilepathUtils::getCanonicalForm).anyMatch(canonicalPath::equals)) {
        created = new TestResource(project, canonicalPath);
      } else {
        if (canonicalPath.endsWith(".lua")) {
          created = new Source(project, canonicalPath);
        } else {
          created = new Resource(project, canonicalPath);
        }
      }
    }
    cache.put(canonicalPath, created);
    return created;
  }

  public Resource getPackage(final String packageName) {
    final ResourceManager newResourceManager = packageManager.find(packageName);
    return new PackageResource(newResourceManager);
  }
}
