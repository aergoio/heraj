/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractInterfaceConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<ContractFunction, Blockchain.Function> contractFunctionConverter =
      new ContractFunctionConverterFactory().create();

  protected final Function<ContractInferface, Blockchain.ABI> domainConverter = 
      domainContractInterface -> {
        logger.trace("Domain contract interface: {}", domainContractInterface);
        return Blockchain.ABI.newBuilder()
            .setVersion(domainContractInterface.getVersion())
            .setLanguage(domainContractInterface.getLanguage())
            .addAllFunctions(domainContractInterface.getFunctions().stream()
                .map(contractFunctionConverter::convertToRpcModel)
                .collect(toList()))
            .build();
      };

  protected final Function<Blockchain.ABI, ContractInferface> rpcConverter = 
      rpcContractInterface -> {
        logger.trace("Rpc contract interface: {}", rpcContractInterface);
        final ContractInferface domainContractInterface = new ContractInferface();
        domainContractInterface.setVersion(rpcContractInterface.getVersion());
        domainContractInterface.setLanguage(rpcContractInterface.getLanguage());
        domainContractInterface.setFunctions(rpcContractInterface.getFunctionsList().stream()
            .map(contractFunctionConverter::convertToDomainModel)
            .collect(toList()));
        return domainContractInterface;
      };

  public ModelConverter<ContractInferface, Blockchain.ABI> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
