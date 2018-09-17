/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import hera.command.AbstractCommand;
import hera.command.BuildProject;
import hera.command.CreateProject;
import hera.command.InstallPackage;
import hera.command.PublishPackage;
import hera.command.TestProject;
import hera.util.MessagePrinter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandFactory {

  protected final MessagePrinter printer;

  protected Command createInternal(final String keyword) {
    switch (keyword) {
      case "init":
        return new CreateProject();
      case "install":
        return new InstallPackage();
      case "build":
        return new BuildProject();
      case "test":
        return new TestProject();
      case "publish":
        return new PublishPackage();
      default:
        return null;
    }
  }

  protected Optional<Command> create(final String keyword) {
    final Command command = createInternal(keyword);
    if (command instanceof AbstractCommand) {
      ((AbstractCommand) command).setPrinter(printer);
    }
    return ofNullable(command);
  }

  /**
   * Create command for {@code args}.
   *
   * @param args user inputs
   *
   * @return command if exists
   */
  public Optional<Command> create(final String[] args) {
    Optional<Command> commandOpt = create(args[0]);
    commandOpt.ifPresent(command -> command.setArguments(asList(args).subList(1, args.length)));
    return commandOpt;
  }

}
