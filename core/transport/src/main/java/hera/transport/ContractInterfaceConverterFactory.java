/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractInterfaceConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<ContractFunction, Blockchain.Function> contractFunctionConverter =
      new ContractFunctionConverterFactory().create();

  protected final Function<ContractInterface, Blockchain.ABI> domainConverter =
      domainContractInterface -> {
        logger.trace("Domain contract interface: {}", domainContractInterface);
        final List<Blockchain.Function> rpcFunctions = new ArrayList<>();
        for (final ContractFunction domainFunction : domainContractInterface.getFunctions()) {
          rpcFunctions.add(contractFunctionConverter.convertToRpcModel(domainFunction));
        }
        return Blockchain.ABI.newBuilder()
            .setVersion(domainContractInterface.getVersion())
            .setLanguage(domainContractInterface.getLanguage())
            .addAllFunctions(rpcFunctions)
            .build();
      };

  protected final Function<Blockchain.ABI, ContractInterface> rpcConverter =
      rpcContractInterface -> {
        logger.trace("Rpc contract interface: {}", rpcContractInterface);
        final List<ContractFunction> domainFunctions = new ArrayList<>();
        for (final Blockchain.Function rpcFunction : rpcContractInterface.getFunctionsList()) {
          domainFunctions.add(contractFunctionConverter.convertToDomainModel(rpcFunction));
        }
        return new ContractInterface(new ContractAddress(BytesValue.EMPTY),
            rpcContractInterface.getVersion(), rpcContractInterface.getLanguage(), domainFunctions);
      };

  public ModelConverter<ContractInterface, Blockchain.ABI> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
