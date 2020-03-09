/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.EventFilter;
import hera.api.transaction.AergoJsonMapper;
import hera.api.transaction.JsonMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import types.Blockchain;
import types.Blockchain.FilterInfo;

public class EventFilterConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final JsonMapper mapper = new AergoJsonMapper();

  protected final ModelConverter<AccountAddress, com.google.protobuf.ByteString>
      accountAddressConverter = new AccountAddressConverterFactory().create();

  protected final Function1<EventFilter, Blockchain.FilterInfo> domainConverter =
      new Function1<EventFilter, Blockchain.FilterInfo>() {

        @Override
        public Blockchain.FilterInfo apply(final EventFilter domainEventFilter) {
          logger.trace("Domain event filter: {}", domainEventFilter);
          final ByteString rpcContractAddress =
              accountAddressConverter.convertToRpcModel(domainEventFilter.getContractAddress());

          final Map<String, Object> orderToValue = new HashMap<>();
          final List<Object> filterArgs = domainEventFilter.getArgs();
          for (int i = 0; i < filterArgs.size(); ++i) {
            orderToValue.put(Integer.toString(i), filterArgs.get(i));
          }

          final FilterInfo rpcEventFilter = Blockchain.FilterInfo.newBuilder()
              .setContractAddress(rpcContractAddress)
              .setEventName(domainEventFilter.getEventName())
              .setBlockfrom(domainEventFilter.getFromBlockNumber())
              .setBlockto(domainEventFilter.getToBlockNumber())
              .setDesc(domainEventFilter.isDecending())
              .setArgFilter(copyFrom(mapper.marshal(orderToValue)))
              .setRecentBlockCnt(domainEventFilter.getRecentBlockCount())
              .build();
          logger.trace("Rpc event filter converted: {}", rpcEventFilter);
          return rpcEventFilter;
        }
      };

  protected final Function1<Blockchain.FilterInfo, EventFilter> rpcConverter =
      new Function1<Blockchain.FilterInfo, EventFilter>() {

        @Override
        public EventFilter apply(Blockchain.FilterInfo rpcEventFilter) {
          throw new UnsupportedOperationException();
        }
      };

  public ModelConverter<EventFilter, Blockchain.FilterInfo> create() {
    return new ModelConverter<EventFilter, Blockchain.FilterInfo>(domainConverter,
        rpcConverter);
  }

}
