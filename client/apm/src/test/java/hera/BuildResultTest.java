/*
 * @copyright defined in LICENSE.txt
 */
package hera;

import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class BuildResultTest {
  @Test
  public void testJson() throws JsonProcessingException {
    final BuildResult buildResult = new BuildResult();
    final String json = new ObjectMapper().writeValueAsString(buildResult);
    assertNotNull(json);
  }

}