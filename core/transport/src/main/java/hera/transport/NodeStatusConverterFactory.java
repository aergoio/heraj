/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.transaction.AergoJsonMapper;
import hera.api.transaction.JsonMapper;
import hera.exception.HerajException;
import java.util.ArrayList;
import org.slf4j.Logger;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class NodeStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  protected final Function1<NodeStatus, Rpc.SingleBytes> domainConverter =
      new Function1<NodeStatus, Rpc.SingleBytes>() {

        @Override
        public Rpc.SingleBytes apply(final NodeStatus domainNodeStatus) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.SingleBytes, NodeStatus> rpcConverter =
      new Function1<Rpc.SingleBytes, NodeStatus>() {

        @Override
        public NodeStatus apply(final Rpc.SingleBytes rpcNodeStatus) {
          try {
            logger.trace("Rpc node status to convert: {}", rpcNodeStatus);
            final byte[] rawNodeStatus = rpcNodeStatus.getValue().toByteArray();
            final NodeStatus domainNodeStatus = rawNodeStatus.length == 0
                ? NodeStatus.newBuilder().moduleStatus(new ArrayList<ModuleStatus>()).build()
                : mapper.unmarshal(BytesValue.of(rawNodeStatus), NodeStatus.class);
            logger.trace("Domain node status converted: {}", domainNodeStatus);
            return domainNodeStatus;
          } catch (Throwable e) {
            throw new HerajException(e);
          }
        }
      };

  public ModelConverter<NodeStatus, Rpc.SingleBytes> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
