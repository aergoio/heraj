/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Abi;
import hera.api.model.AbiSet;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class AbiSetConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Abi, Blockchain.Function> abiConverter =
      new AbiConverterFactory().create();

  protected final Function<AbiSet, Blockchain.ABI> domainConverter = domainAbiSet -> {
    logger.trace("Domain status: {}", domainAbiSet);
    return Blockchain.ABI.newBuilder()
        .setVersion(domainAbiSet.getVersion())
        .setLanguage(domainAbiSet.getLanguage())
        .addAllFunctions(
            domainAbiSet.getAbis().stream().map(abiConverter::convertToRpcModel).collect(toList()))
        .build();
  };

  protected final Function<Blockchain.ABI, AbiSet> rpcConverter = rpcAbiSet -> {
    logger.trace("Blockchain status: {}", rpcAbiSet);
    final AbiSet domainAbi = new AbiSet();
    domainAbi.setVersion(rpcAbiSet.getVersion());
    domainAbi.setLanguage(rpcAbiSet.getLanguage());
    domainAbi.setAbis(rpcAbiSet.getFunctionsList().stream().map(abiConverter::convertToDomainModel)
        .collect(toList()));
    return domainAbi;
  };

  public ModelConverter<AbiSet, Blockchain.ABI> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
