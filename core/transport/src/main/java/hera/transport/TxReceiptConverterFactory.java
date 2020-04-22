/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static hera.util.TransportUtils.parseToBlockHash;
import static hera.util.TransportUtils.parseToTxHash;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Event;
import hera.api.model.TxReceipt;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

@ApiAudience.Private
@ApiStability.Unstable
public class TxReceiptConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<Event, Blockchain.Event> eventConverter =
      new EventConverterFactory().create();

  protected final Function1<TxReceipt, Blockchain.Receipt> domainConverter =
      new Function1<TxReceipt, Blockchain.Receipt>() {

        @Override
        public Blockchain.Receipt apply(final TxReceipt domainReceipt) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.Receipt, TxReceipt> rpcConverter =
      new Function1<Blockchain.Receipt, TxReceipt>() {

        @Override
        public TxReceipt apply(final Blockchain.Receipt rpcReceipt) {
          logger.trace("Rpc tx receipt to convert: {}", rpcReceipt);
          final AccountAddress accountAddress =
              accountAddressConverter.convertToDomainModel(rpcReceipt.getContractAddress());

          final List<Event> domainEvents = new ArrayList<>();
          for (final Blockchain.Event rpcEvent : rpcReceipt.getEventsList()) {
            domainEvents.add(eventConverter.convertToDomainModel(rpcEvent));
          }

          // for transaction, keep itself, but for receipt, no empty fee
          Aer usedFee = parseToAer(rpcReceipt.getFeeUsed());
          if (Aer.EMPTY.equals(usedFee)) {
            usedFee = Aer.ZERO;
          }
          final TxReceipt domainTxReceipt = TxReceipt.newBuilder()
              .accountAddress(accountAddress)
              .status(rpcReceipt.getStatus())
              .result(rpcReceipt.getRet())
              .txHash(parseToTxHash(rpcReceipt.getTxHash()))
              .feeUsed(usedFee)
              .cumulativeFeeUsed(parseToAer(rpcReceipt.getCumulativeFeeUsed()))
              .blockNumber(rpcReceipt.getBlockNo())
              .blockHash(parseToBlockHash(rpcReceipt.getBlockHash()))
              .indexInBlock(rpcReceipt.getTxIndex())
              .sender(accountAddressConverter.convertToDomainModel(rpcReceipt.getFrom()))
              .recipient(accountAddressConverter.convertToDomainModel(rpcReceipt.getTo()))
              .feeDelegation(rpcReceipt.getFeeDelegation())
              .gasUsed(rpcReceipt.getGasUsed())
              .build();
          logger.trace("Domain tx receipt converted: {}", domainTxReceipt);
          return domainTxReceipt;
        }
      };

  public ModelConverter<TxReceipt, Blockchain.Receipt> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
