/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import static hera.util.HexUtils.encode;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import org.junit.Test;
import org.mockito.Mock;

public class TransferTest extends AbstractTestCase {

  @Mock
  protected AccountRepository accountRepository;

  protected final String sender = encode(randomUUID().toString().getBytes());
  protected final String recipient = encode(randomUUID().toString().getBytes());

  @Test
  public void testExecute() throws Exception {
    final Transfer command = new Transfer(accountRepository);
    command.setArguments(asList(sender, recipient, "30"));
    command.execute(new CommandContext());
  }

}