/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractInterfaceConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<ContractFunction, Blockchain.Function> contractFunctionConverter =
      new ContractFunctionConverterFactory().create();

  protected final Function1<ContractInterface, Blockchain.ABI> domainConverter =
      new Function1<ContractInterface, Blockchain.ABI>() {

        @Override
        public Blockchain.ABI apply(final ContractInterface domainContractInterface) {
          logger.trace("Domain contract interface: {}", domainContractInterface);
          final List<Blockchain.Function> rpcFunctions = new ArrayList<Blockchain.Function>();
          for (final ContractFunction domainFunction : domainContractInterface.getFunctions()) {
            rpcFunctions.add(contractFunctionConverter.convertToRpcModel(domainFunction));
          }
          return Blockchain.ABI.newBuilder()
              .setVersion(domainContractInterface.getVersion())
              .setLanguage(domainContractInterface.getLanguage())
              .addAllFunctions(rpcFunctions)
              .build();
        }
      };

  protected final Function1<Blockchain.ABI, ContractInterface> rpcConverter =
      new Function1<Blockchain.ABI, ContractInterface>() {

        @Override
        public ContractInterface apply(final Blockchain.ABI rpcContractInterface) {
          logger.trace("Rpc contract interface: {}", rpcContractInterface);
          final List<ContractFunction> domainFunctions = new ArrayList<ContractFunction>();
          for (final Blockchain.Function rpcFunction : rpcContractInterface.getFunctionsList()) {
            domainFunctions.add(contractFunctionConverter.convertToDomainModel(rpcFunction));
          }
          return new ContractInterface(new ContractAddress(BytesValue.EMPTY),
              rpcContractInterface.getVersion(), rpcContractInterface.getLanguage(),
              domainFunctions);
        }
      };

  public ModelConverter<ContractInterface, Blockchain.ABI> create() {
    return new ModelConverter<ContractInterface, Blockchain.ABI>(domainConverter, rpcConverter);
  }

}
