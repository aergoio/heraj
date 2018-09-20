/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.util.DangerousConsumer;
import org.junit.Before;
import org.junit.Test;

public class BuildServiceTest {

  protected BuildService buildService;

  @Before
  public void setUp() {
    buildService = new BuildService();
  }

  @Test
  public void testAddListener() throws Exception {
    final DangerousConsumer<BuildSummary> listener = mock(DangerousConsumer.class);
    buildService.addListener(listener);
    buildService.save(new BuildDetails());
    verify(listener).accept(any(BuildSummary.class));
  }

  @Test
  public void testSave() {
    for (int i = 0 ; i<10000 ; ++i) {
      buildService.save(new BuildDetails());
    }
  }

  @Test
  public void testList() {
    final BuildDetails result1 = new BuildDetails();
    final BuildDetails result2 = new BuildDetails();
    final BuildDetails result3 = new BuildDetails();
    final BuildDetails result4 = new BuildDetails();
    final BuildDetails result5 = new BuildDetails();
    buildService.save(result1);
    buildService.save(result2);
    buildService.save(result3);
    buildService.save(result4);
    buildService.save(result5);
    assertEquals(2, buildService.list(result2.getUuid(), 3).size());
    assertEquals(3, buildService.list(null, 3).size());
    assertEquals(3, buildService.list(result5.getUuid(), 3).size());
    assertNull(buildService.list(randomUUID().toString(), 3));
  }

  @Test
  public void testGet() {
    final BuildDetails details = new BuildDetails();
    buildService.save(details);
    assertNotNull(buildService.get(details.getUuid()));
  }

}