/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import hera.AbstractTestCase;
import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import org.jline.reader.LineReader;
import org.junit.Test;
import org.mockito.Mock;

public class LockAccountTest extends AbstractTestCase {

  @Mock
  protected CommandContext commandContext;

  @Mock
  protected AccountRepository accountRepository;

  protected final String address = randomUUID().toString();
  protected final String password = randomUUID().toString();

  @Test
  public void testExecute() throws Exception {
    // Given
    final LineReader lineReader = mock(LineReader.class);
    when(lineReader.readLine(anyString(), eq('*'))).thenReturn(password);
    when(commandContext.getLineReader()).thenReturn(lineReader);

    // When
    final LockAccount command = new LockAccount(accountRepository);
    command.setArguments(ImmutableList.of(address));
    final CommandResult commandResult = command.execute(commandContext);

    // Then
    logger.debug("Result: {}", commandResult);
    assertNotNull(commandResult);
    verify(accountRepository).lock(eq(address), eq(password));

  }

}