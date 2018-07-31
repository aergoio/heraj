/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveAccount extends AbstractCommand {

  protected final AccountRepository accountRepository;

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    if (0 == arguments.size()) {
      return new CommandResult(() -> "Address needed");
    } else if (1 < arguments.size()) {
      return new CommandResult(() -> "Many arguments found");
    }
    final String address = arguments.get(0);
    final boolean result = accountRepository.delete(address);
    return new CommandResult(() -> {
      if (result) {
        return address + " removed";
      } else {
        return address + " not found";
      }
    });
  }
}
