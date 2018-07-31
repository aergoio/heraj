/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import hera.repl.SecuredAccount;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateAccount extends AbstractCommand {

  protected final AccountRepository accountRepository;

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    if (0 < arguments.size()) {
      return new CommandResult(() -> "Many arguments found");
    }
    final String password = context.getLineReader()
        .readLine("Input your password >>", '*');
    final SecuredAccount account = accountRepository.create(password);
    return new CommandResult(() -> {
      final StringBuilder buffer = new StringBuilder();
      buffer.append(account.getEncodedAddress() + " created");
      return buffer.toString();
    });
  }
}
