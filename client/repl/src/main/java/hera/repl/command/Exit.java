/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.CommandContext;
import hera.repl.CommandResult;

public class Exit extends AbstractCommand {

  @Override
  public CommandResult execute(final CommandContext context) {
    context.close();
    return new CommandResult(() -> "Bye bye.");
  }
}
