/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.ServerInfo;
import hera.exception.RpcException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import types.Rpc;

public class ServerInfoConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<ServerInfo, Rpc.ServerInfo> domainConverter =
      new Function1<ServerInfo, Rpc.ServerInfo>() {

        @Override
        public Rpc.ServerInfo apply(final ServerInfo domainServerInfo) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.ServerInfo, ServerInfo> rpcConverter =
      new Function1<Rpc.ServerInfo, ServerInfo>() {

        @Override
        public ServerInfo apply(final Rpc.ServerInfo rpcServerInfo) {
          try {
            logger.trace("Rpc server info to convert: {}", rpcServerInfo);
            final Map<String, String> domainStatus = rpcServerInfo.getStatusMap();
            final Map<String, Map<String, String>> domainConfig =
                new HashMap<String, Map<String, String>>();
            final Map<String, Rpc.ConfigItem> rpcConfig = rpcServerInfo.getConfigMap();
            for (final String key : rpcConfig.keySet()) {
              final Rpc.ConfigItem rpcConfigItem = rpcConfig.get(key);
              domainConfig.put(key, rpcConfigItem.getPropsMap());
            }
            final ServerInfo domainServerInfo = ServerInfo.newBuilder()
                .status(domainStatus)
                .config(domainConfig)
                .build();
            logger.trace("Domain server info converted: {}", domainServerInfo);
            return domainServerInfo;
          } catch (Throwable e) {
            throw new RpcException(e);
          }
        }
      };

  public ModelConverter<ServerInfo, Rpc.ServerInfo> create() {
    return new ModelConverter<ServerInfo, Rpc.ServerInfo>(domainConverter, rpcConverter);
  }

}
