/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractResult;
import hera.client.ContractResultImpl;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Rpc;

public class ContractResultConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<ContractResult, Rpc.SingleBytes> domainConverter =
      domainContractResult -> {
        throw new UnsupportedOperationException();
      };

  protected final Function<Rpc.SingleBytes, ContractResult> rpcConverter = rpcContractResult -> {
    logger.trace("Blockchain contract result: {}", rpcContractResult);
    final ContractResult contractResult =
        new ContractResultImpl(rpcContractResult.getValue().toByteArray());
    return contractResult;
  };

  public ModelConverter<ContractResult, Rpc.SingleBytes> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
