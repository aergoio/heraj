/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractFunction;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractFunctionConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<ContractFunction, Blockchain.Function> domainConverter =
      domainContractFunction -> {
        logger.trace("Domain contract function: {}", domainContractFunction);
        return Blockchain.Function.newBuilder().setName(domainContractFunction.getName())
            .addAllArguments(domainContractFunction.getArgumentNames().stream()
                .map(n -> Blockchain.FnArgument.newBuilder().setName(n).build()).collect(toList()))
            .build();
      };

  protected final Function<Blockchain.Function, ContractFunction> rpcConverter =
      rpcContractFunction -> {
        logger.trace("Rpc contract function: {}", rpcContractFunction);
        final ContractFunction domainContractFunction =
            new ContractFunction(rpcContractFunction.getName(), rpcContractFunction
                .getArgumentsList().stream().map(Blockchain.FnArgument::getName).collect(toList()));
        return domainContractFunction;
      };

  public ModelConverter<ContractFunction, Blockchain.Function> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
