/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Abi;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class AbiConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Abi, Blockchain.Function> domainConverter = domainAbi -> {
    logger.trace("Domain status: {}", domainAbi);
    return Blockchain.Function.newBuilder()
        .setName(domainAbi.getName())
        .addAllArguments(domainAbi.getArgumentNames().stream()
            .map(n -> Blockchain.FnArgument.newBuilder().setName(n).build()).collect(toList()))
        .build();
  };

  protected final Function<Blockchain.Function, Abi> rpcConverter = rpcAbi -> {
    logger.trace("Blockchain status: {}", rpcAbi);
    final Abi domainAbi = new Abi();
    domainAbi.setName(rpcAbi.getName());
    domainAbi.setArgumentNames(
        rpcAbi.getArgumentsList().stream().map(Blockchain.FnArgument::getName).collect(toList()));
    return domainAbi;
  };

  public ModelConverter<Abi, Blockchain.Function> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
