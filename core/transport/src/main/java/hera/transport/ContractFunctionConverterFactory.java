/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.ContractFunction;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractFunctionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<ContractFunction, Blockchain.Function> domainConverter =
      new Function1<ContractFunction, Blockchain.Function>() {

        @Override
        public Blockchain.Function apply(final ContractFunction domainContractFunction) {
          logger.trace("Domain contract function: {}", domainContractFunction);
          final List<Blockchain.FnArgument> rpcArguments = new ArrayList<Blockchain.FnArgument>();
          for (final String name : domainContractFunction.getArgumentNames()) {
            rpcArguments.add(Blockchain.FnArgument.newBuilder().setName(name).build());
          }
          return Blockchain.Function.newBuilder().setName(domainContractFunction.getName())
              .addAllArguments(rpcArguments)
              .build();
        }
      };

  protected final Function1<Blockchain.Function, ContractFunction> rpcConverter =
      new Function1<Blockchain.Function, ContractFunction>() {

        @Override
        public ContractFunction apply(final Blockchain.Function rpcContractFunction) {
          logger.trace("Rpc contract function: {}", rpcContractFunction);
          final List<String> domainArguments = new ArrayList<String>();
          for (final Blockchain.FnArgument rpcArgument : rpcContractFunction.getArgumentsList()) {
            domainArguments.add(rpcArgument.getName());
          }
          final ContractFunction domainContractFunction =
              new ContractFunction(rpcContractFunction.getName(), domainArguments);
          return domainContractFunction;
        }
      };

  public ModelConverter<ContractFunction, Blockchain.Function> create() {
    return new ModelConverter<ContractFunction, Blockchain.Function>(domainConverter, rpcConverter);
  }

}
