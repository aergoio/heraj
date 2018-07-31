/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import java.util.List;

public interface Command {
  void setArguments(List<String> arguments);

  CommandResult execute(CommandContext context) throws Exception;
}
