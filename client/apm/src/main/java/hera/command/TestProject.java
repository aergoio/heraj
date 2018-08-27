/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ObjectUtils.nvl;
import static java.util.Collections.EMPTY_LIST;

import hera.Builder;
import hera.BuilderFactory;
import hera.ProjectFile;
import hera.build.res.Project;
import hera.build.web.model.BuildDetails;
import hera.test.AthenaContext;
import hera.test.LuaRunner;
import hera.test.TestResult;
import hera.test.TestResultCollector;
import hera.util.SourcePrinter;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class TestProject extends AbstractCommand {

  @Getter
  @Setter
  protected BuilderFactory builderFactory = new BuilderFactory();

  @Setter
  protected Consumer<TestResultCollector> reporter = (testReporter) -> {
    testReporter.getResults().forEach(testSuite -> {
      System.out.println(testSuite.toString());
      testSuite.getTestCases().forEach(testCase -> {
        System.out.println("   " + testCase);
      });
    });
  };

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);
    final ProjectFile projectFile = readProject();
    final Project project = new Project(".", projectFile);
    final Builder builder = new BuilderFactory().create(project);

    final List<String> testPaths = nvl(projectFile.getTests(), EMPTY_LIST);
    AthenaContext.clear();
    try {
      for (final String testPath : testPaths) {
        final BuildDetails buildDetails = builder.build(testPath);
        final String script = buildDetails.getResult();
        final TestResult testResult = new LuaRunner().run(script);
        logger.debug("Test {} => {}", testPath, testResult);
        if (!testResult.isSuccess()) {
          logger.debug("Lua Script:\n{}", new SourcePrinter().apply(script));
        }
      }
    } finally {
      final AthenaContext context = AthenaContext.getContext();
      final TestResultCollector testResultCollector = context.getTestReporter();
      logger.debug("TestResultCollector: {}", testResultCollector);
      reporter.accept(testResultCollector);
    }
  }
}
