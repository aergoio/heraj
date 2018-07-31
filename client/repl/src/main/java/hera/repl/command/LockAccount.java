/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LockAccount extends AbstractCommand {

  protected final AccountRepository accountRepository;

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    if (arguments.size() < 1) {
      return new CommandResult(() ->
          "Address needed");
    } else if (1 < arguments.size()) {
      return new CommandResult(() -> "Many arguments found");
    }

    final String address = arguments.get(0);
    final String password = context.getLineReader().readLine("Input the password >> ", '*');
    try {
      accountRepository.lock(address, password);
      return new CommandResult(() -> address + " locked");
    } catch (final IllegalArgumentException e) {
      return new CommandResult(() -> "Fail to lock " + address);
    }
  }
}
