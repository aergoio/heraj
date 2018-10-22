package hera.api.model;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

/**
 * FIXME
 */
public class NodeStatusTest {
  //@Test
  public void shouldNotThrowExceptionForNullModules() {
    final NodeStatus nodeStatus = new NodeStatus();
    assertNotNull(nodeStatus.toString());
  }

  //@Test
  public void shouldNotThrowExceptionForEmptyModules() {
    final NodeStatus nodeStatus = new NodeStatus();
    nodeStatus.setModuleStatus(new ArrayList<>());
    assertNotNull(nodeStatus.toString());
  }

}