/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertTrue;

import hera.Builder;
import hera.FileContent;
import hera.FileSet;
import hera.ProjectFile;
import hera.build.MonitorServer;
import hera.build.Resource;
import hera.build.ResourceManager;
import hera.build.res.BuildResource;
import hera.build.res.PackageResource;
import hera.build.res.Project;
import hera.build.res.TestResource;
import hera.exception.NoBuildTargetException;
import hera.util.FileWatcher;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import lombok.Getter;
import lombok.Setter;

public class BuildProject extends AbstractCommand {

  @Getter
  @Setter
  protected FileContent output;

  protected Builder builder;

  protected void build(final Project project) throws IOException {
    final ProjectFile projectFile = project.getProjectFile();
    final String buildTarget = projectFile.getTarget();
    if (null == buildTarget) {
      throw new NoBuildTargetException();
    }
    final FileSet fileSet = builder.build(buildTarget);
    logger.info("Fileset: {}", fileSet);
    fileSet.copyTo(project.getPath());
  }

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    boolean serverMode = false;
    int port = -1;
    for (int i = 0, n = arguments.size(); i < n; ++i) {
      final String argument = arguments.get(i);
      if ("--watch".equals(argument)) {
        serverMode = true;
      } else if ("--port".equals(argument)) {
        assertTrue(++i < arguments.size());
        final String portStr = arguments.get(i);
        port = Integer.parseInt(portStr);
      }
    }

    final ProjectFile projectFile = readProject();
    final Project project = new Project(".", projectFile);
    builder = new BuilderFactory().create(project);
    build(project);
    if (serverMode) {
      final MonitorServer server = new MonitorServer();
      if (0 < port) {
        server.setPort(port);
      }
      server.boot();


      final ResourceManager resourceManager = builder.getResourceManager();
      resourceManager.addResourceChangeListener((event) -> {
        logger.info("Resource changed: {}", event.getResource());
        final Resource changedResource = event.getResource();
        if (changedResource instanceof BuildResource) {
          logger.trace("Skip build resource: {}", changedResource.getLocation());
          return;
        } else if (changedResource instanceof PackageResource) {
          logger.trace("Skip package resource: {}", changedResource.getLocation());
          return;
        } else if (changedResource instanceof TestResource) {
          logger.trace("Skip test resource: {}", changedResource.getLocation());
          return;
        }
        try {
          build(project);
        } catch (IOException ex) {
          // Handle exception
        }
      });
      final WatchService watchService = FileSystems.getDefault().newWatchService();
      final FileWatcher fileWatcher = new FileWatcher(watchService, project.getPath());
      fileWatcher.addServerListener(resourceManager);
      fileWatcher.run();
    }
  }
}
