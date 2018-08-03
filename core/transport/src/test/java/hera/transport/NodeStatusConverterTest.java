/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.NodeStatus;
import java.util.ArrayList;
import org.junit.Test;
import types.Rpc;

public class NodeStatusConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<NodeStatus, Rpc.NodeStatus> converter = new NodeStatusConverterFactory()
        .create();

    final NodeStatus domainNodeStatus = new NodeStatus();
    domainNodeStatus.setModuleStatus(new ArrayList<>());
    final Rpc.NodeStatus rpcModuleStatus = converter.convertToRpcModel(domainNodeStatus);
    final NodeStatus actualDomainNodeStatus = converter.convertToDomainModel(rpcModuleStatus);
    assertNotNull(actualDomainNodeStatus);
  }

}
