/*
 * @copyright defined in LICENSE.txt
 */

package hera.exec;

import static java.lang.System.exit;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Command;
import hera.CommandFactory;
import java.util.Optional;
import org.slf4j.Logger;

public class ApmLauncher {
  protected static final Logger logger = getLogger(ApmLauncher.class);

  protected static void printHelp() {
    System.err.println("No command!");
  }

  /**
   * apm's entry point.
   *
   * @param args user arguments
   */
  public static void main(final String[] args) {
    logger.info("Apm launched");
    if (args.length < 1) {
      printHelp();
      exit(-1);
    }

    final CommandFactory commandFactory = new CommandFactory();
    final Optional<Command> commandOpt = commandFactory.create(args);
    if (!commandOpt.isPresent()) {
      printHelp();
      exit(-1);
    }

    commandOpt.ifPresent(command -> {
      try {
        logger.trace("{} starting...", command);
        command.execute();
        exit(0);
      } catch (final Throwable throwable) {
        final String errorMessage = throwable.getMessage();
        if (null != errorMessage) {
          System.err.println(errorMessage);
          logger.error("Fail to execute {}", command, throwable);
        } else {
          throwable.printStackTrace();
        }
        exit(-1);
      }
    });
  }
}
