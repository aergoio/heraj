/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.Receipt;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Receipt, Blockchain.Receipt> domainConverter = domainRecepit -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Blockchain.Receipt, Receipt> rpcConverter = rpcReceipt -> {
    logger.trace("Blockchain status: {}", rpcReceipt);
    final Receipt domainReceipt = new Receipt();
    domainReceipt.setReceipt(AccountAddress.of(rpcReceipt.getContractAddress().toByteArray()));
    domainReceipt.setStatus(rpcReceipt.getStatus());
    domainReceipt.setRet(rpcReceipt.getRet());
    return domainReceipt;
  };

  public ModelConverter<Receipt, Blockchain.Receipt> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
