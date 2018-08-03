/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ModuleStatus;
import hera.util.Pair;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;
import types.Rpc.InternalStat;

public class ModuleStatusConverterFactory {

  protected final Logger logger = getLogger(getClass());

  protected final Function<ModuleStatus, Rpc.ModuleStatus> domainConverter =
      domainModuleStatus -> {
        logger.trace("Domain status: {}", domainModuleStatus);

        final List<Rpc.InternalStat> internalStats = domainModuleStatus.getInternalStatus().stream()
            .map(s -> InternalStat.newBuilder()
                    .setName(s.v1)
                    .setStat(s.v2)
                    .build())
            .collect(toList());

        return Rpc.ModuleStatus.newBuilder()
            .setName(domainModuleStatus.getModuleName())
            .addAllStat(internalStats)
            .build();
      };

  protected final Function<Rpc.ModuleStatus, ModuleStatus> rpcConverter =
      rpcModuleStatus -> {
        logger.trace("Blockchain status: {}", rpcModuleStatus);

        final List<Pair<String, Double>> internalStatus = rpcModuleStatus.getStatList().stream()
            .map(s -> new Pair<>(s.getName(), s.getStat()))
            .collect(toList());

        final ModuleStatus domainModuleStatus = new ModuleStatus();
        domainModuleStatus.setModuleName(rpcModuleStatus.getName());
        domainModuleStatus.setInternalStatus(internalStatus);

        return domainModuleStatus;
      };

  public ModelConverter<ModuleStatus, Rpc.ModuleStatus> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
