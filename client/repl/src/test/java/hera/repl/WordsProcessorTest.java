/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import org.junit.Test;

public class WordsProcessorTest extends AbstractTestCase {

  @Test
  public void testFind() {
    final WordsProcessor wordsProcessor = new WordsProcessor();
    wordsProcessor.add(asList("command1", "command2"), () -> mock(Command.class));
    assertTrue(wordsProcessor.find(asList("command1", "command2")).isPresent());
    assertFalse(wordsProcessor.find(asList("command2", "command1")).isPresent());
  }
}