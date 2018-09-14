/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ObjectUtils.nvl;
import static java.util.Collections.EMPTY_LIST;

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
import hera.test.TestFile;
import hera.test.TestResult;
import hera.test.TestResultCollector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      System.out.println(testFile.toString());
      testFile.getSuites().forEach(testSuite -> {
        System.out.println(testSuite.toString());
        testSuite.getTestCases().forEach(testCase -> {
          System.out.println("   " + testCase);
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
        final TestFile testFile = testReporter.getCurrentTestFile();
        if (!testResult.isSuccess()) {
          testFile.setSuccess(false);
          testFile.setError(testResult.getError());
        }

        path2result.put(testPath, executable);
        logger.debug("Test {} => {}", testPath, testResult);
        if (!testResult.isSuccess()) {
          final LuaErrorInformation error = testResult.getError();
          final int lineNumber = error.getLineNumber();
          logger.debug("Lua Script:\n{}", executable.toString(lineNumber - 5, lineNumber + 5));
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
