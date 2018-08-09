/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;

public class NodeStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<ModuleStatus, Rpc.ModuleStatus> moduleStatusConverter =
      new ModuleStatusConverterFactory().create();

  protected final Function<NodeStatus, Rpc.NodeStatus> domainConverter =
      domainNodeStatus -> {
        logger.trace("Domain status: {}", domainNodeStatus);
        final List<Rpc.ModuleStatus> rpcModuleStatus = domainNodeStatus.getModuleStatus().stream()
            .map(s -> moduleStatusConverter.convertToRpcModel(s))
            .collect(toList());
        return Rpc.NodeStatus.newBuilder()
            .addAllStatus(rpcModuleStatus)
            .build();
      };

  protected final Function<Rpc.NodeStatus, NodeStatus> rpcConverter =
      rpcNodeStatus -> {
        logger.trace("Blockchain status: {}", rpcNodeStatus);
        final NodeStatus domainNodeStatus = new NodeStatus();
        List<ModuleStatus> moduleStatus = rpcNodeStatus.getStatusList().stream()
            .map(s -> moduleStatusConverter.convertToDomainModel(s))
            .collect(toList());
        domainNodeStatus.setModuleStatus(moduleStatus);
        return domainNodeStatus;
      };

  public ModelConverter<NodeStatus, Rpc.NodeStatus> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
