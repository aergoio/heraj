/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.repl.AccountRepository;
import hera.repl.Command;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import hera.repl.SecuredAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListAccounts extends AbstractCommand implements Command {

  protected final AccountRepository accountRepository;

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    List<SecuredAccount> accounts = accountRepository.list();
    return new CommandResult(() -> {
      final StringBuilder buffer = new StringBuilder();
      accounts.forEach(account -> {
        buffer.append(account.getEncodedAddress() + "\n");
      });
      buffer.append(accounts.size() + " account(s) found");
      return buffer.toString();
    });
  }
}
