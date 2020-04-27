/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.StateVariable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractInterfaceConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<StateVariable, Blockchain.StateVar> stateVariableConverter =
      new StateVariableConverterFactory().create();

  protected final Function1<ContractInterface,
      Blockchain.ABI> domainConverter = new Function1<ContractInterface, Blockchain.ABI>() {

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
            final ContractFunction domainContractFunction = new ContractFunction(
                rpcFunction.getName(), domainArguments, rpcFunction.getPayable(),
                rpcFunction.getView(), rpcFunction.getFeeDelegation());
            domainFunctions.add(domainContractFunction);
          }

          final List<StateVariable> domainStateVariables = new ArrayList<StateVariable>();
          for (final Blockchain.StateVar rpcStateVariable : rpcContractInterface
              .getStateVariablesList()) {
            domainStateVariables.add(stateVariableConverter.convertToDomainModel(rpcStateVariable));
          }

          final ContractInterface domainContractInterface = ContractInterface
              .newBuilder()
              .address(ContractAddress.EMPTY)
              .version(rpcContractInterface.getVersion())
              .language(rpcContractInterface.getLanguage())
              .functions(domainFunctions)
              .stateVariables(domainStateVariables)
              .build();
          logger.trace("Domain contract interface converted: {}", domainContractInterface);
          return domainContractInterface;
        }
      };

  public ModelConverter<ContractInterface, Blockchain.ABI> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
