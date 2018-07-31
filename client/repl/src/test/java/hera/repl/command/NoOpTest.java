/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.AbstractTestCase;
import hera.repl.CommandContext;
import org.junit.Test;

public class NoOpTest extends AbstractTestCase {

  @Test
  public void testExecute() throws Exception {
    new NoOp().execute(new CommandContext());
  }

}