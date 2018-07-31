/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import hera.repl.SecuredAccount;
import org.junit.Test;
import org.mockito.Mock;

public class ListAccountsTest extends AbstractTestCase {

  @Mock
  protected AccountRepository accountRepository;

  @Test
  public void testExecute() throws Exception {
    // Given
    final SecuredAccount account =
        SecuredAccount.of(randomUUID().toString(), randomUUID().toString());
    when(accountRepository.list()).thenReturn(asList(account));

    // When
    final ListAccounts command = new ListAccounts(accountRepository);
    final CommandResult commandResult = command.execute(new CommandContext());
    logger.debug("Result: {}", commandResult);

    // Then
    assertNotNull(commandResult);
  }
}