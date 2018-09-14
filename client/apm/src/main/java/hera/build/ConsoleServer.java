package hera.build;

import static hera.build.web.model.BuildSummary.BUILD_FAIL;
import static hera.build.web.model.BuildSummary.SUCCESS;
import static hera.build.web.model.BuildSummary.TEST_FAIL;
import static hera.util.StringUtils.multiply;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.server.AbstractServer;
import hera.server.ServerStatus;
import hera.test.TestCase;
import hera.test.TestFile;
import hera.test.TestSuite;
import hera.util.StringUtils;
import java.io.PrintStream;
import java.util.Date;

public class ConsoleServer extends AbstractServer {
  protected final PrintStream out = System.out;

  protected static final String CLEAR_SCREEN = "\033[2J";
  protected static final String GO_HOME = "\033[H";

  protected static final String COLOR_RESET   = "\u001B[0m";
  protected static final String COLOR_BLACK   = "\u001B[30m";
  protected static final String COLOR_RED     = "\u001B[31m";
  protected static final String COLOR_GREEN   = "\u001B[32m";
  protected static final String COLOR_YELLOW  = "\u001B[33m";
  protected static final String COLOR_BLUE    = "\u001B[34m";
  protected static final String COLOR_PURPLE  = "\u001B[35m";
  protected static final String COLOR_CYAN    = "\u001B[36m";
  protected static final String COLOR_WHITE   = "\u001B[37m";
  protected static final String COLOR_GRAY   = "\u001b[38;5;242m";

  protected static final String BACKGROUND_GREEN  = "\u001b[42m";
  protected static final String BACKGROUND_RED    = "\u001b[41m";
  protected static final String BACKGROUND_BLUE   = "\u001b[44m";
  protected static final String BACKGROUND_BRIGHT_BLUE   = "\u001b[44;1m";

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
    println();
    if (SUCCESS == details.getState() || null == details.getError()) {
      printTest(details);
    } else {
      printError(details);
    }

    out.flush();
  }

  protected void clearScreen() {
    out.print(CLEAR_SCREEN);
    out.print(GO_HOME);
  }

  protected void printResult(final BuildSummary summary) {
    final String now = new Date().toString();
    switch (summary.getState()) {
      case SUCCESS:
        print(" SUCCESS ", BACKGROUND_GREEN + COLOR_BLACK);// 9
        print(" " + summary.getElapsedTime() + "ms elapsed", COLOR_GREEN);// 11 + l
        println(multiply(" ", 38 - ("" + summary.getElapsedTime()).length()) + now, COLOR_GRAY);
        break;
      case BUILD_FAIL:
        print(" ERROR! ", BACKGROUND_RED + COLOR_BLACK);//8
        println(multiply(" ", 50) + now, COLOR_GRAY);
        break;
      case TEST_FAIL:
        print(" TEST FAILURE! ", BACKGROUND_RED + COLOR_BLACK);//15
        println(multiply(" ", 43) + now, COLOR_GRAY);
        break;
      default:
        throw new IllegalArgumentException("Unknown state: " + summary.getState());
    }
  }

  protected void printError(final BuildDetails buildDetails) {
    println(buildDetails.getError(), COLOR_RED);
  }

  protected void printTest(final BuildDetails buildDetails) {
    buildDetails.getUnitTestReport().forEach(this::print);
  }

  protected void print(final TestFile testFile) {
    print(" ");
    print(" F ", BACKGROUND_BLUE + COLOR_BLACK);
    print(" ");
    if (testFile.isSuccess()) {
      println(testFile.getFilename());
    } else {
      println(testFile.getFilename(), COLOR_RED);
    }
    testFile.getSuites().forEach(this::print);
  }

  protected void print(final TestSuite testSuite) {
    final long nFailures = testSuite.getFailures();
    if (0 < nFailures) {
      print("    * " + testSuite.getName() + "(");
      print("" + testSuite.getSuccesses(), COLOR_RED);
      println(" / " + testSuite.getTests() + ")");
    } else {
      println("   * " + testSuite.getName()
          + "(" + testSuite.getSuccesses() + " / " + testSuite.getTests() + ")");
    }
    testSuite.getTestCases().forEach(this::print);
  }

  protected void print(final TestCase testCase) {
    print("    ");
    if (testCase.isSuccess()) {
      print(" - " + testCase.getName());
      print(" " + (testCase.getEndTime() - testCase.getStartTime()) + "ms", COLOR_GRAY);
    } else {
      print(" - " + testCase.getName() + " - " + testCase.getErrorMessage() + " ", BACKGROUND_RED);
    }
    println();
  }

  protected void print(final String text) {
    out.print(text);
  }

  protected void print(final String text, final String color) {
    out.print(color + text + COLOR_RESET);
  }

  protected void println() {
    out.println();
  }

  protected void println(final String text) {
    out.println(text);
  }

  protected void println(final String text, final String color) {
    out.println(color + text + COLOR_RESET);
  }
}
