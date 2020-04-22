/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToBytesValue;
import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.TxReceipt;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

public class ContractTxReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Event, Blockchain.Event> eventConverter =
      new EventConverterFactory().create();

  protected final ModelConverter<TxReceipt, Blockchain.Receipt> txReceiptConverter =
      new TxReceiptConverterFactory().create();

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
          logger.trace("Rpc contract tx receipt to convert: {}", rpcReceipt);

          final List<Event> domainEvents = new ArrayList<>();
          for (final Blockchain.Event rpcEvent : rpcReceipt.getEventsList()) {
            domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
          }

          final ContractTxReceipt domainContractTxReceipt = ContractTxReceipt.newBuilder()
              .txReceipt(txReceiptConverter.convertToDomainModel(rpcReceipt))
              .bloom(parseToBytesValue(rpcReceipt.getBloom()))
              .events(unmodifiableList(domainEvents))
              .build();
          logger.trace("Domain contract tx receipt converted: {}", domainContractTxReceipt);
          return domainContractTxReceipt;
        }
      };

  public ModelConverter<ContractTxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
