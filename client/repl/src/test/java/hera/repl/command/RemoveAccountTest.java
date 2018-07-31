/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import hera.AbstractTestCase;
import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import org.junit.Test;
import org.mockito.Mock;

public class RemoveAccountTest extends AbstractTestCase {

  @Mock
  protected AccountRepository accountRepository;

  protected final String address = randomUUID().toString();

  @Test
  public void testExecute() throws Exception {
    // Given

    // When
    final RemoveAccount command = new RemoveAccount(accountRepository);
    command.setArguments(ImmutableList.of(address));
    final CommandResult commandResult = command.execute(new CommandContext());

    // Then
    logger.debug("Result: {}", commandResult);
    assertNotNull(commandResult);
    verify(accountRepository).delete(eq(address));
  }


}