/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import static org.junit.Assert.assertFalse;

import hera.AbstractTestCase;
import hera.repl.CommandContext;
import org.junit.Test;

public class ExitTest extends AbstractTestCase {
  @Test
  public void testExecute() {
    final CommandContext commandContext = new CommandContext();
    new Exit().execute(commandContext);
    assertFalse(commandContext.isAlive());
  }

}