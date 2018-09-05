/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static hera.util.FileWatcher.FILE_ADDED;
import static hera.util.FileWatcher.FILE_CHANGED;
import static hera.util.FileWatcher.FILE_REMOVED;
import static hera.util.FileWatcher.RESET;
import static hera.util.FilepathUtils.getCanonicalForm;
import static hera.util.ObjectUtils.equal;
import static hera.util.ObjectUtils.nvl;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

import hera.ProjectFile;
import hera.build.res.BuildResource;
import hera.build.res.PackageResource;
import hera.build.res.Project;
import hera.build.res.Source;
import hera.build.res.TestResource;
import hera.server.ServerEvent;
import hera.server.ServerListener;
import hera.util.FilepathUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ResourceManager implements ServerListener {
  protected static final Set<Integer> eventFilter =
      unmodifiableSet(new HashSet(asList(FILE_ADDED, FILE_CHANGED, FILE_REMOVED, RESET)));

  protected final transient Logger logger = getLogger(getClass());

  protected final Map<String, Resource> cache = new HashMap<>();

  @Getter
  protected final Project project;

  @Getter
  @Setter
  protected PackageManager packageManager = new PackageManager();

  protected final Set<ResourceChangeListener> resourceChangeListeners = new HashSet<>();

  public void addResourceChangeListener(final ResourceChangeListener listener) {
    resourceChangeListeners.add(listener);
  }

  public void removeResourceChangeListener(final ResourceChangeListener listener) {
    resourceChangeListeners.remove(listener);
  }

  protected void fireEvent(final ResourceChangeEvent event) {
    for (final ResourceChangeListener listener : resourceChangeListeners) {
      listener.handle(event);
    }
  }

  /**
   * Return resouce for {@code basePath}.
   *
   * @param path resoruce basePath
   *
   * @return resoruce for basePath
   */
  public synchronized Resource getResource(final String path) {
    logger.trace("Path: {}", path);
    final ProjectFile projectFile = project.getProjectFile();
    final String canonicalPath = getCanonicalForm(path);
    logger.trace("Canonical basePath: {}", canonicalPath);
    final Resource cached = cache.get(canonicalPath);
    if (null != cached) {
      return cached;
    }
    Resource created = null;
    if (equal(canonicalPath, getCanonicalForm(projectFile.getTarget()))) {
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
    logger.debug("{} added", canonicalPath);
    return created;
  }

  public Resource getPackage(final String packageName) {
    final ResourceManager newResourceManager = packageManager.find(packageName);
    return new PackageResource(newResourceManager);
  }

  @Override
  public void handle(final ServerEvent event) {
    if (!eventFilter.contains(event.getType())) {
      logger.trace("Unhandled event: {}", event);
      return;
    }

    final int eventType = event.getType();
    logger.trace("Event type: {}", eventType);
    switch (eventType) {
      case RESET:
        logger.info("Reset builder");
        break;
      case FILE_ADDED:
      case FILE_CHANGED:
      case FILE_REMOVED:
        final String path =
            getCanonicalForm(((Path) event.getNewData()).toAbsolutePath().toString());
        final String projectPath =
            getCanonicalForm(project.getPath().toAbsolutePath().toString());
        final Path relativePath =
            Paths.get(projectPath).relativize(Paths.get(path));
        logger.trace("Project path: {}, Path: {}, Relative path: {}",
            projectPath, path, relativePath);
        final String canonicalPath = getCanonicalForm(relativePath.toString());
        logger.trace("Project relative path: {}", canonicalPath);
        final Resource cached = cache.get(canonicalPath);
        if (null == cached) {
          logger.debug("No resource in cache: {}", canonicalPath);
          logger.debug("Cache: {}", cache);
          return;
        }

        logger.info("{} changed: {}", cached, event.getType());
        fireEvent(new ResourceChangeEvent(cached));
        break;
      default:
        throw new IllegalStateException("Unreachable branch");
    }
  }
}
