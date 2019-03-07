/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.EventFilter;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Blockchain;

public class EventFilterConverterTest extends AbstractTestCase {

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<EventFilter, Blockchain.FilterInfo> converter =
        new EventFilterConverterFactory().create();

    final EventFilter domainEventFilter = EventFilter.newBuilder(contractAddress)
        .build();
    final Blockchain.FilterInfo converted = converter.convertToRpcModel(domainEventFilter);
    assertNotNull(converted);
  }

}
