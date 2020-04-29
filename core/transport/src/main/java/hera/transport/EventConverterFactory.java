/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToBlockHash;
import static hera.util.TransportUtils.parseToBytesValue;
import static hera.util.TransportUtils.parseToTxHash;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.Event;
import hera.api.transaction.AergoJsonMapper;
import hera.api.transaction.JsonMapper;
import java.util.List;
import org.slf4j.Logger;
import types.Blockchain;

@ApiAudience.Private
@ApiStability.Unstable
public class EventConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString>
      accountAddressConverter = new AccountAddressConverterFactory().create();

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
                .convertToDomainModel(rpcEvent.getContractAddress())
                .adapt(ContractAddress.class);
            final List<Object> deserializedArgs = mapper
                .unmarshal(parseToBytesValue(rpcEvent.getJsonArgsBytes()), List.class);
            final Event domainEvent = Event.newBuilder()
                .from(contractAddress)
                .name(rpcEvent.getEventName())
                .args(deserializedArgs)
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
    return new ModelConverter<>(domainConverter,
        rpcConverter);
  }

}
