/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import hera.api.model.ContractTxReceipt;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<ContractTxReceipt, Blockchain.Receipt> domainConverter =
      domainRecepit -> {
        throw new UnsupportedOperationException();
      };

  protected final Function<Blockchain.Receipt, ContractTxReceipt> rpcConverter = rpcReceipt -> {
    logger.trace("Blockchain status: {}", rpcReceipt);
    final ContractTxReceipt domainReceipt = new ContractTxReceipt();
    domainReceipt
        .setContractAddress(ContractAddress.of(rpcReceipt.getContractAddress().toByteArray()));
    domainReceipt.setStatus(rpcReceipt.getStatus());
    domainReceipt.setRet(rpcReceipt.getRet());
    return domainReceipt;
  };

  public ModelConverter<ContractTxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
