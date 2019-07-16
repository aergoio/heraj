/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractTxReceipt;
import org.slf4j.Logger;
import types.Blockchain;

public class ReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<ContractTxReceipt, Blockchain.Receipt> domainConverter =
      new Function1<ContractTxReceipt, Blockchain.Receipt>() {

        @Override
        public Blockchain.Receipt apply(final ContractTxReceipt domainRecepit) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.Receipt, ContractTxReceipt> rpcConverter =
      new Function1<Blockchain.Receipt, ContractTxReceipt>() {

        @Override
        public ContractTxReceipt apply(final Blockchain.Receipt rpcReceipt) {
          logger.trace("Rpc tx receipt to convert: {}", rpcReceipt);
          final AccountAddress accountAddress =
              accountAddressConverter.convertToDomainModel(rpcReceipt.getContractAddress());
          final ContractTxReceipt domainTxReceipt = ContractTxReceipt.newBuilder()
              .contractAddress(new ContractAddress(accountAddress.getBytesValue()))
              .status(rpcReceipt.getStatus())
              .ret(rpcReceipt.getRet())
              .build();
          logger.trace("Domain tx receipt converted: {}", domainTxReceipt);
          return domainTxReceipt;
        }
      };

  public ModelConverter<ContractTxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<ContractTxReceipt, Blockchain.Receipt>(domainConverter, rpcConverter);
  }

}
