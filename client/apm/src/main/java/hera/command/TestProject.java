/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.test.TestReportNodeResult.Failure;
import static hera.util.ObjectUtils.nvl;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Optional.ofNullable;

import hera.Builder;
import hera.BuilderFactory;
import hera.ProjectFile;
import hera.build.ResourceManager;
import hera.build.res.Project;
import hera.build.web.model.BuildDetails;
import hera.test.AthenaContext;
import hera.test.LuaErrorInformation;
import hera.test.LuaRunner;
import hera.test.LuaSource;
import hera.test.TestReportNode;
import hera.test.TestResult;
import hera.test.TestResultCollector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class TestProject extends AbstractCommand {

  @Getter
  @Setter
  protected BuilderFactory builderFactory = project -> new Builder(new ResourceManager(project));

  @Setter
  protected Consumer<TestResultCollector> reporter = (testReporter) -> {
    testReporter.getResults().forEach(testFile -> {
      if (testFile.isSuccess()) {
        getPrinter().println(" <green>o</green> %s", testFile.getName());
      } else {
        final Optional<String> errorMessage =
            ofNullable(testFile.getResultDetail())
                .map(obj -> (LuaErrorInformation) obj)
                .map(LuaErrorInformation::getMessage);
        if (errorMessage.isPresent()) {
          getPrinter().println(" <red>x</red> %s - <red>%s</red>",
              testFile.getName(), errorMessage.get());
        } else {
          getPrinter().println(" <red>x</red> %s",
              testFile.getName());
        }
      }
      testFile.getChildren().forEach(child -> {
        final TestReportNode<?> node = (TestReportNode<?>) child;
        if (node.isSuccess()) {
          getPrinter().println("  <green>o</green> %s (%d/%d)",
              node.getName(), node.getTheNumberOfSuccesses(), node.getTheNumberOfTests());
        } else {
          getPrinter().println("  <red>x</red> %s (%d/%d)",
              node.getName(), node.getTheNumberOfSuccesses(), node.getTheNumberOfTests());
        }
        node.getChildren().forEach(testCase -> {
          if (testCase.isSuccess()) {
            getPrinter().println("   <green>o</green> %s", testCase.getName());
          } else {
            getPrinter().println("   <red>x</red> %s - <red>%s</red>",
                testCase.getName(), testCase.getResultDetail());
          }
        });
      });
    });
  };

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    final ProjectFile projectFile = readProject();
    final Project project = new Project(".", projectFile);
    final Builder builder = builderFactory.create(project);
    final List<String> testPaths = nvl(projectFile.getTests(), EMPTY_LIST);
    final Map<String, LuaSource> path2result = new HashMap<>();

    AthenaContext.clear();
    try {
      final TestResultCollector testReporter = AthenaContext.getContext().getTestReporter();
      for (final String testPath : testPaths) {
        final BuildDetails buildDetails = builder.build(testPath);
        final LuaSource executable = new LuaSource(buildDetails.getResult());
        testReporter.clear();

        logger.trace("Executing test...");
        testReporter.start(testPath);
        final TestResult testResult = new LuaRunner().run(executable);
        final TestReportNode<LuaErrorInformation> testFile = testReporter.getCurrentTestFile();
        if (!testResult.isSuccess()) {
          logger.info("{} failed", testFile.getName());
          testFile.setResult(Failure);
          testFile.setResultDetail(testResult.getError());
        }

        path2result.put(testPath, executable);
        logger.debug("Test {} => {}", testPath, testResult);
        if (!testResult.isSuccess()) {
          final LuaErrorInformation error = testResult.getError();
          final int lineNumber = error.getLineNumber();
          logger.debug("Lua Script:\n{}",
              executable.toString(lineNumber - 5, lineNumber + 5, asList(lineNumber)));
        }
        testReporter.end();
      }
    } finally {
      final AthenaContext context = AthenaContext.getContext();
      final TestResultCollector testResultCollector = context.getTestReporter();
      logger.debug("TestResultCollector: {}", testResultCollector);
      reporter.accept(testResultCollector);
    }
  }
}
