/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.build.web.model.BuildSummary.BUILD_FAIL;
import static hera.build.web.model.BuildSummary.SUCCESS;
import static hera.build.web.model.BuildSummary.TEST_FAIL;
import static hera.util.ExceptionUtils.buildExceptionMessage;
import static hera.util.ValidationUtils.assertTrue;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;

import hera.Builder;
import hera.ProjectFile;
import hera.build.ConsoleServer;
import hera.build.Resource;
import hera.build.ResourceChangeEvent;
import hera.build.ResourceManager;
import hera.build.WebServer;
import hera.build.res.BuildResource;
import hera.build.res.PackageResource;
import hera.build.res.Project;
import hera.build.web.model.BuildDetails;
import hera.test.TestReportNode;
import hera.util.DangerousConsumer;
import hera.util.FileWatcher;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class BuildProject extends AbstractCommand {

  protected Builder builder;

  protected Project project;

  protected List<DangerousConsumer<BuildDetails>> buildListeners = new ArrayList<>();

  protected FileWatcher createFileWatcher() {
    final FileWatcher fileWatcher = new FileWatcher(project.getPath().toFile());
    fileWatcher.addIgnore(".git");
    fileWatcher.addServerListener(builder.getResourceManager());
    fileWatcher.run();
    return fileWatcher;
  }

  protected BuildDetails build(final Project project, final boolean runTests) {
    if (logger.isTraceEnabled()) {
      final StringJoiner joiner = new StringJoiner("\n\t", "\t", "");
      asList(new Exception().getStackTrace()).stream()
          .map(StackTraceElement::toString)
          .forEach(joiner::add);
      logger.trace("Build triggered:\n{}", joiner.toString());
    }
    final ProjectFile projectFile = project.getProjectFile();
    final String buildTarget = projectFile.getTarget();
    final BuildDetails buildDetails = new BuildDetails();
    final long startTimestamp = currentTimeMillis();
    if (null == buildTarget) {
      buildDetails.setState(BUILD_FAIL);
    } else {
      try {
        buildDetails.copyFrom(builder.build(buildTarget));
        final String contents = buildDetails.getResult();
        try (final Writer out = Files.newBufferedWriter(project.getPath().resolve(buildTarget))) {
          out.write(contents);
        }
        if (runTests) {
          try {
            final TestProject testProject = new TestProject();
            testProject.setBuilderFactory(p -> builder);
            testProject.setReporter(testResultCollector -> {
              final Collection<TestReportNode> testResults = testResultCollector.getResults();
              if (buildDetails.getState() == SUCCESS
                  && testResults.stream().anyMatch(testFile -> !testFile.isSuccess())) {
                buildDetails.setState(TEST_FAIL);
              }
              buildDetails.setUnitTestReport(testResults);
            });
            testProject.execute();
          } catch (final Throwable ex) {
            buildDetails.setError(buildExceptionMessage("Error in unit test", ex));
          }
        }
      } catch (final Throwable buildException) {
        buildDetails.setState(BUILD_FAIL);
        buildDetails.setError(buildException.getMessage());
      }
    }
    final long endTimestamp = currentTimeMillis();
    buildDetails.setElapsedTime(endTimestamp - startTimestamp);

    this.buildListeners.forEach(listener -> {
      try {
        listener.accept(buildDetails);
      } catch (final Throwable ex) {
        logger.trace("Listener {} throws exception", listener, ex);
      }
    });
    return buildDetails;
  }

  protected void startWebServer(final int port) {
    final WebServer webServer = new WebServer(port);
    webServer.setProjectFile(this.project.getProjectFile());
    webServer.boot(true);
    this.buildListeners.add(webServer.getBuildService()::save);
  }

  protected void startConsoleServer() {
    final ConsoleServer consoleServer = new ConsoleServer();
    consoleServer.setPrinter(getPrinter());
    this.buildListeners.add(consoleServer::process);
    consoleServer.boot();
  }

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    final int COMMAND_MODE = 1;
    final int CONSOLE_MODE = 2;
    final int WEB_MODE = 3;
    int mode = COMMAND_MODE;
    int port = -1;
    for (int i = 0, n = arguments.size(); i < n; ++i) {
      final String argument = arguments.get(i);
      if ("--watch".equals(argument)) {
        if (mode == COMMAND_MODE) {
          mode = CONSOLE_MODE;
        }
      } else if ("--port".equals(argument)) {
        mode = WEB_MODE;
        assertTrue(++i < arguments.size());
        final String portStr = arguments.get(i);
        port = Integer.parseInt(portStr);
      }
    }

    final ProjectFile projectFile = readProject();
    project = new Project(".", projectFile);
    final ResourceManager resourceManager = new ResourceManager(project);
    builder = new Builder(resourceManager);
    switch (mode) {
      case COMMAND_MODE:
        final BuildDetails buildDetails = build(project, false);
        switch (buildDetails.getState()) {
          case SUCCESS:
            getPrinter().println("Successful to build.");
            getPrinter().println("Target: <green>%s</green>",
                projectFile.getTargetPath(getProjectHome()));
            break;
          case BUILD_FAIL:
            getPrinter().println("<red>Fail to build.</red>");
            final String errorMessage = buildDetails.getError();
            getPrinter().println("Cause: %s", errorMessage);
            break;
          case TEST_FAIL:
            throw new IllegalStateException();
          default:
            throw new IllegalStateException();
        }
        break;
      case WEB_MODE:
        startWebServer(port);
        startConsoleServer();
        build(project, true);
        resourceManager.addResourceChangeListener(this::resourceChanged);
        createFileWatcher();
        break;
      case CONSOLE_MODE:
        startConsoleServer();
        build(project, true);
        resourceManager.addResourceChangeListener(this::resourceChanged);
        createFileWatcher();
        break;
      default:
        throw new IllegalStateException();
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
    build(project, true);
  }
}
