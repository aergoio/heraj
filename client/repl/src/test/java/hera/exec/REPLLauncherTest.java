/*
 * @copyright defined in LICENSE.txt
 */

package hera.exec;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import hera.REPL;
import hera.AbstractTestCase;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({REPLLauncher.class, REPL.class})
public class REPLLauncherTest extends AbstractTestCase {

  @Test
  public void testRun() throws Exception {
    final REPL repl = mock(REPL.class);
    whenNew(REPL.class).withAnyArguments().thenReturn(repl);
    final REPLLauncher replLauncher = new REPLLauncher();
    replLauncher.run();;
  }

}