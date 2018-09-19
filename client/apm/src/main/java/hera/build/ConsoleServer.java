package hera.build;

import static hera.build.web.model.BuildSummary.BUILD_FAIL;
import static hera.build.web.model.BuildSummary.SUCCESS;
import static hera.build.web.model.BuildSummary.TEST_FAIL;
import static java.util.Optional.ofNullable;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.server.AbstractServer;
import hera.server.ServerStatus;
import hera.test.TestReportNode;
import hera.util.DummyMessagePrinter;
import hera.util.MessagePrinter;
import java.io.IOException;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

public class ConsoleServer extends AbstractServer {

  protected static final String CLEAR_SCREEN = "\033[2J";
  protected static final String GO_HOME = "\033[H";

  @Getter
  @Setter
  protected MessagePrinter printer = DummyMessagePrinter.getInstance();

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

    try {
      printer.flush();
    } catch (final IOException e) {
      logger.trace("Ignore exception", e);
    }
  }

  protected void clearScreen() {
    printer.print(CLEAR_SCREEN);
    printer.print(GO_HOME);
  }

  protected void printResult(final BuildSummary summary) {
    final String now = new Date().toString();
    switch (summary.getState()) {
      case SUCCESS:
        printer.println("<bg_green><black> SUCCESS </black></bg_green>      "
                + "<green>%-30s</green><bright_black>%s</bright_black>",
            summary.getElapsedTime() + " ms elapsed", now);
        break;
      case BUILD_FAIL:
        printer.println("<bg_red><black> ERROR! </black></bg_red>       "
                + "%30s<bright_black>%s</bright_black>",
            " ", now);
        break;
      case TEST_FAIL:
        printer.println("<bg_red><black> TEST FAILURE! </black></bg_red>"
                + "%30s<bright_black>%s</bright_black>",
            " ", now);
        break;
      default:
        throw new IllegalArgumentException("Unknown state: " + summary.getState());
    }
  }

  protected void printError(final BuildDetails buildDetails) {
    printer.println("<red>%s</red>", buildDetails.getError());
  }

  protected void printTest(final BuildDetails buildDetails) {
    ofNullable(buildDetails).map(BuildDetails::getUnitTestReport).ifPresent(testReport -> {
      testReport.forEach(this::print);
    });
  }

  protected void print(final TestReportNode node) {
    if (node.isSuccess()) {
      printer.println(" <bg_blue> F </bg_blue> %s", node.getName());
    } else {
      printer.println(" <bg_blue> F </bg_blue> <red>$s</red>", node.getName());
    }
    node.getChildren().forEach(child -> this.printSuite((TestReportNode) child));
  }

  protected void printSuite(final TestReportNode node) {
    final long nFailures = node.getTheNumberOfFailures();
    if (0 < nFailures) {
      printer.print("    * %s(<red>%s</red> / %s)",
          node.getName(), node.getTheNumberOfSuccesses(), node.getTheNumberOfTests());
    } else {
      printer.println("   * %s(%s / %s)",
          node.getName(), node.getTheNumberOfSuccesses(), node.getTheNumberOfTests());
    }
    node.getChildren().forEach(child -> this.printCase((TestReportNode) child));
  }

  protected void printCase(final TestReportNode node) {
    if (node.isSuccess()) {
      printer.println("     -  %s <bright_black>%s ms</bright_black>",
          node.getName(), (node.getEndTime() - node.getStartTime()));
    } else {
      printer.println("    <bg_red> - %s - %s </bg_red>",
          node.getName(), node.getResultDetail());
    }
  }
}
