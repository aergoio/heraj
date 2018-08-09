/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import hera.command.BuildProject;
import hera.command.CreateProject;
import hera.command.InstallPackage;
import hera.command.PublishPackage;
import java.util.Optional;

public class CommandFactory {

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

  protected Optional<Command> create(final String keyword) {
    switch (keyword) {
      case "init":
        return ofNullable(new CreateProject());
      case "install":
        return ofNullable(new InstallPackage());
      case "build":
        return ofNullable(new BuildProject());
      case "publish":
        return ofNullable(new PublishPackage());
      default:
        return empty();
    }
  }
}
