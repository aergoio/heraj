/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import hera.build.web.model.BuildDetails;
import org.junit.Before;
import org.junit.Test;

public class BuildServiceTest {
  protected BuildDetails result1 = new BuildDetails();
  protected BuildDetails result2 = new BuildDetails();
  protected BuildDetails result3 = new BuildDetails();
  protected BuildDetails result4 = new BuildDetails();
  protected BuildDetails result5 = new BuildDetails();

  protected BuildService buildService;

  @Before
  public void setUp() {
    buildService = new BuildService();
    buildService.save(result1);
    buildService.save(result2);
    buildService.save(result3);
    buildService.save(result4);
    buildService.save(result5);
  }

  @Test
  public void testList() {
    assertEquals(2, buildService.list(result2.getUuid(), 3).size());
    assertEquals(3, buildService.list(null, 3).size());
    assertEquals(3, buildService.list(result5.getUuid(), 3).size());
    assertNull(buildService.list(randomUUID().toString(), 3));
  }

}