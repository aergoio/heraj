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

  protected final Function1<ContractInterface, Blockchain.ABI> domainConverter =
      new Function1<ContractInterface, Blockchain.ABI>() {

        @Override
        public Blockchain.ABI apply(final ContractInterface domainContractInterface) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.ABI, ContractInterface> rpcConverter =
      new Function1<Blockchain.ABI, ContractInterface>() {

        @Override
        public ContractInterface apply(final Blockchain.ABI rpcContractInterface) {
          logger.trace("Rpc contract interface to convert: {}", rpcContractInterface);
          final List<ContractFunction> domainFunctions = new ArrayList<ContractFunction>();
          for (final Blockchain.Function rpcFunction : rpcContractInterface.getFunctionsList()) {
            final List<String> domainArguments = new ArrayList<String>();
            for (final Blockchain.FnArgument rpcArgument : rpcFunction.getArgumentsList()) {
              domainArguments.add(rpcArgument.getName());
            }
            final ContractFunction domainContractFunction =
                new ContractFunction(rpcFunction.getName(), domainArguments,
                    rpcFunction.getPayable(), rpcFunction.getView());
            domainFunctions.add(domainContractFunction);
          }
          final ContractInterface domainContractInterface =
              new ContractInterface(new ContractAddress(BytesValue.EMPTY),
                  rpcContractInterface.getVersion(),
                  rpcContractInterface.getLanguage(),
                  domainFunctions);
          logger.trace("Domain contract interface converted: {}", domainContractInterface);
          return domainContractInterface;
        }
      };

  public ModelConverter<ContractInterface, Blockchain.ABI> create() {
    return new ModelConverter<ContractInterface, Blockchain.ABI>(domainConverter, rpcConverter);
  }

}
