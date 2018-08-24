/*
 * @copyright defined in LICENSE.txt
 */
package hera;

import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hera.build.web.model.BuildDetails;
import org.junit.Test;

public class BuildResultTest {
  @Test
  public void testJson() throws JsonProcessingException {
    final BuildDetails buildDetails = new BuildDetails();
    final String json = new ObjectMapper().writeValueAsString(buildDetails);
    assertNotNull(json);
  }

}