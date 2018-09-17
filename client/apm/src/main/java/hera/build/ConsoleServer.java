package hera.build;

import static hera.build.web.model.BuildSummary.BUILD_FAIL;
import static hera.build.web.model.BuildSummary.SUCCESS;
import static hera.build.web.model.BuildSummary.TEST_FAIL;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.server.AbstractServer;
import hera.server.ServerStatus;
import hera.test.TestCase;
import hera.test.TestFile;
import hera.test.TestSuite;
import hera.util.MessagePrinter;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

public class ConsoleServer extends AbstractServer {

  protected static final String CLEAR_SCREEN = "\033[2J";
  protected static final String GO_HOME = "\033[H";

  @Getter
  @Setter
  protected MessagePrinter printer;

  @Override
  public void boot() {
    changeStatus(ServerStatus.PROCESSING);
  }

  @Override
  public void down() {
    changeStatus(ServerStatus.TERMINATED);
  }

  /**
   * Process build result.
   *
   * @param details build result
   */
  public void process(final BuildDetails details) {
    if (!isStatus(ServerStatus.PROCESSING)) {
      return;
    }

    clearScreen();
    printResult(details);
    printer.println();
    if (SUCCESS == details.getState() || null == details.getError()) {
      printTest(details);
    } else {
      printError(details);
    }

    printer.flush();
  }

  protected void clearScreen() {
    printer.print(CLEAR_SCREEN);
    printer.print(GO_HOME);
  }

  protected void printResult(final BuildSummary summary) {
    final String now = new Date().toString();
    switch (summary.getState()) {
      case SUCCESS:
        printer.println("<bg_green><black> SUCCESS </black></gb_green>      "
                + "<green>%-30s</green><bright_black>$30s</bright_black>",
            summary.getElapsedTime() + "elapsed", now);
        break;
      case BUILD_FAIL:
        printer.println("<bg_red><black> ERROR! </black></bg_red>       "
                + "<bright_black>%30s</bright_black>",
            now);
        break;
      case TEST_FAIL:
        printer.println("<bg_red><black> TEST FAILURE! </black></bg_red>"
                + "<bright_black>$30s</bright_black>",
            now);
        break;
      default:
        throw new IllegalArgumentException("Unknown state: " + summary.getState());
    }
  }

  protected void printError(final BuildDetails buildDetails) {
    printer.println("<red>%s</red>", buildDetails.getError());
  }

  protected void printTest(final BuildDetails buildDetails) {
    buildDetails.getUnitTestReport().forEach(this::print);
  }

  protected void print(final TestFile testFile) {
    if (testFile.isSuccess()) {
      printer.println(" <bg_blue> F </bg_blue> %s", testFile.getFilename());
    } else {
      printer.println(" <bg_blue> F </bg_blue> <red>$s</red>", testFile.getFilename());
    }
    testFile.getSuites().forEach(this::print);
  }

  protected void print(final TestSuite testSuite) {
    final long nFailures = testSuite.getFailures();
    if (0 < nFailures) {
      printer.print("    * %s(<red>%s</red> / %s)",
          testSuite.getName(), testSuite.getSuccesses(), testSuite.getTests());
    } else {
      printer.println("   * %s(%s / %s)",
          testSuite.getName(), testSuite.getSuccesses(), testSuite.getTests());
    }
    testSuite.getTestCases().forEach(this::print);
  }

  protected void print(final TestCase testCase) {
    if (testCase.isSuccess()) {
      printer.println("     -  %s<bright_black>%sms</bright_black>",
          testCase.getName(), (testCase.getEndTime() - testCase.getStartTime()));
    } else {
      printer.println("    <bg_red> - %s - %s </bg_red>",
          testCase.getName(), testCase.getErrorMessage());
    }
  }
}
