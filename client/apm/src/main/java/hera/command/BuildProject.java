/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ExceptionUtils.buildExceptionMessage;
import static hera.util.ValidationUtils.assertTrue;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import hera.Builder;
import hera.BuilderFactory;
import hera.ProjectFile;
import hera.build.MonitorServer;
import hera.build.Resource;
import hera.build.ResourceChangeEvent;
import hera.build.ResourceManager;
import hera.build.res.BuildResource;
import hera.build.res.PackageResource;
import hera.build.res.Project;
import hera.build.res.TestResource;
import hera.build.web.model.BuildDetails;
import hera.exception.NoBuildTargetException;
import hera.util.FileWatcher;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.WatchService;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

public class BuildProject extends AbstractCommand {

  @Getter
  @Setter
  protected Optional<MonitorServer> monitorServer = empty();

  @Getter
  @Setter
  protected FileWatcher fileWatcher;

  protected Builder builder;

  protected Project project;

  protected MonitorServer createMonitorServer(final int port) {
    final MonitorServer monitorServer = new MonitorServer();
    if (0 < port) {
      monitorServer.setPort(port);
    }
    monitorServer.setProjectFilePath(getProjectFile());
    this.monitorServer = of(monitorServer);
    return monitorServer;
  }

  protected FileWatcher createFileWatcher() throws IOException {
    final WatchService watchService = FileSystems.getDefault().newWatchService();
    fileWatcher = new FileWatcher(watchService, project.getPath());
    fileWatcher.addServerListener(builder.getResourceManager());
    fileWatcher.run();
    return fileWatcher;
  }

  protected void build(final Project project, final boolean runTests) throws IOException {
    final ProjectFile projectFile = project.getProjectFile();
    final String buildTarget = projectFile.getTarget();
    if (null == buildTarget) {
      throw new NoBuildTargetException();
    }
    final BuildDetails buildDetails = builder.build(buildTarget);
    final String contents = buildDetails.getResult();
    try (final Writer out = Files.newBufferedWriter(project.getPath().resolve(buildTarget))) {
      out.write(contents);
    }

    if (runTests) {
      try {
        final TestProject testProject = new TestProject();
        testProject.setBuilderFactory(p -> builder);
        testProject.setReporter(testResultCollector -> {
          buildDetails.setUnitTestReport(testResultCollector.getResults());
        });
        testProject.execute();
      } catch (final Throwable ex) {
        buildDetails.setError(buildExceptionMessage("Error in unit test", ex));
      }
    }
    monitorServer.ifPresent(server -> {
      server.getBuildService().save(buildDetails);
    });
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
    project = new Project(".", projectFile);
    builder = new Builder(new ResourceManager(project));
    if (serverMode) {
      createMonitorServer(port).boot(true);
      build(project, true);
      final ResourceManager resourceManager = builder.getResourceManager();
      resourceManager.addResourceChangeListener(this::resourceChanged);
      createFileWatcher();
    } else {
      build(project, false);
    }
  }

  protected void resourceChanged(final ResourceChangeEvent event) {
    logger.info("Resource changed: {}", event);
    final Resource changedResource = event.getResource();
    if (changedResource instanceof PackageResource) {
      logger.trace("Skip package resource: {}", changedResource.getLocation());
      return;
    } else if (changedResource instanceof BuildResource) {
      logger.trace("Skip build resource: {}", changedResource.getLocation());
      return;
    }
    try {
      build(project, true);
    } catch (IOException ex) {
      // Handle exception
    }
  }
}
