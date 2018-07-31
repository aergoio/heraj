/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.Command;
import hera.repl.CommandContext;
import hera.repl.CommandResult;

public class NoOp extends AbstractCommand implements Command {

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    return new CommandResult(() -> "");
  }
}
