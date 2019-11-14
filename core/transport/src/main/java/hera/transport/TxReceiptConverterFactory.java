/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static hera.util.TransportUtils.parseToBlockHash;
import static hera.util.TransportUtils.parseToBytesValue;
import static hera.util.TransportUtils.parseToTxHash;
import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.client.internal.ContractResultImpl;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

public class TxReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Event, Blockchain.Event> eventConverter =
      new EventConverterFactory().create();

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

          final List<Event> domainEvents = new ArrayList<>();
          for (final Blockchain.Event rpcEvent : rpcReceipt.getEventsList()) {
            domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
          }

          final ContractTxReceipt domainTxReceipt = ContractTxReceipt.newBuilder()
              .contractAddress(new ContractAddress(accountAddress.getBytesValue()))
              .status(rpcReceipt.getStatus())
              .ret(new ContractResultImpl(BytesValue.of(rpcReceipt.getRet().getBytes())))
              .txHash(parseToTxHash(rpcReceipt.getTxHash()))
              .feeUsed(parseToAer(rpcReceipt.getFeeUsed()))
              .cumulativeFeeUsed(parseToAer(rpcReceipt.getCumulativeFeeUsed()))
              .bloom(parseToBytesValue(rpcReceipt.getBloom()))
              .events(unmodifiableList(domainEvents))
              .blockNumber(rpcReceipt.getBlockNo())
              .blockHash(parseToBlockHash(rpcReceipt.getBlockHash()))
              .indexInBlock(rpcReceipt.getTxIndex())
              .sender(accountAddressConverter.convertToDomainModel(rpcReceipt.getFrom()))
              .recipient(accountAddressConverter.convertToDomainModel(rpcReceipt.getTo()))
              .feeDelegation(rpcReceipt.getFeeDelegation())
              .build();
          logger.trace("Domain tx receipt converted: {}", domainTxReceipt);
          return domainTxReceipt;
        }
      };

  public ModelConverter<ContractTxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<ContractTxReceipt, Blockchain.Receipt>(domainConverter, rpcConverter);
  }

}
