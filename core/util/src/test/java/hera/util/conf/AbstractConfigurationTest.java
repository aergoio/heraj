/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AbstractConfigurationTest {

  /**
   * input and expected result.
   * 
   * @return {@link List}
   */
  @Parameters
  public static List<Object[]> inputAndExpected() {
    return asList(new Object[] {"v1.v2", new String[] {"v1", "v2"}},
        new Object[] {"v1.v2.v3", new String[] {"v1", "v2", "v3"}});
  }

  @Parameter(0)
  public String input;

  @Parameter(1)
  public Object[] expected;

  protected AbstractConfiguration configuration = spy(AbstractConfiguration.class);

  @Test
  public void testGetFragments() {
    final String[] fragments = configuration.getFragments(input);
    assertArrayEquals(expected, fragments);
  }

  @Test
  public void testParse() {
    final String privatekeys = "abc,\ndef,\naaa\nbbb";
    final List<String> parsed = configuration.parse(privatekeys);
    assertEquals(4, parsed.size());
  }

  @Test(expected = Error.class)
  public void shouldThrowException() {
    configuration.getFragments(".test");
  }
}
