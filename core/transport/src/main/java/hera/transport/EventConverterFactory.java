/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToBlockHash;
import static hera.util.TransportUtils.parseToTxHash;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.Event;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

public class EventConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = new ObjectMapper();

  protected final ModelConverter<AccountAddress,
      com.google.protobuf.ByteString> accountAddressConverter =
          new AccountAddressConverterFactory().create();

  protected final Function1<Event, Blockchain.Event> domainConverter =
      new Function1<Event, Blockchain.Event>() {

        @Override
        public Blockchain.Event apply(final Event domainEvent) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.Event, Event> rpcConverter =
      new Function1<Blockchain.Event, Event>() {

        @SuppressWarnings("unchecked")
        @Override
        public Event apply(Blockchain.Event rpcEvent) {
          logger.trace("Rpc event: {}", rpcEvent);

          try {
            final ContractAddress contractAddress = accountAddressConverter
                .convertToDomainModel(rpcEvent.getContractAddress()).adapt(ContractAddress.class);
            final Event domainEvent = Event.newBuilder()
                .from(contractAddress)
                .name(rpcEvent.getEventName())
                .args(mapper.readValue(rpcEvent.getJsonArgs(), List.class))
                .index(rpcEvent.getEventIdx())
                .txHash(parseToTxHash(rpcEvent.getTxHash()))
                .indexInBlock(rpcEvent.getTxIndex())
                .blockHash(parseToBlockHash(rpcEvent.getBlockHash()))
                .blockNumber(rpcEvent.getBlockNo())
                .build();
            logger.trace("Rpc event converted: {}", domainEvent);
            return domainEvent;
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
      };

  public ModelConverter<Event, Blockchain.Event> create() {
    return new ModelConverter<Event, Blockchain.Event>(domainConverter,
        rpcConverter);
  }

}
