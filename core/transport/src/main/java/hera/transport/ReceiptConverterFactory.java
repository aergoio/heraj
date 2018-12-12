/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractTxReceipt;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class ReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<ContractTxReceipt, Blockchain.Receipt> domainConverter =
      domainRecepit -> {
        throw new UnsupportedOperationException();
      };

  protected final Function<Blockchain.Receipt, ContractTxReceipt> rpcConverter = rpcReceipt -> {
    logger.trace("Rpc contract tx receipt: {}", rpcReceipt);
    final AccountAddress accountAddress =
        accountAddressConverter.convertToDomainModel(rpcReceipt.getContractAddress());
    return new ContractTxReceipt(
        accountAddress.adapt(ContractAddress.class).get(),
        rpcReceipt.getStatus(),
        rpcReceipt.getRet());
  };

  public ModelConverter<ContractTxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
