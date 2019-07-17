/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Event;
import java.net.UnknownHostException;
import org.junit.Test;
import types.Blockchain;

public class EventConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() throws UnknownHostException {
    final ModelConverter<Event, Blockchain.Event> converter = new EventConverterFactory().create();

    final Blockchain.Event rpcEvent = Blockchain.Event.newBuilder()
        .setContractAddress(copyFrom(accountAddress.getBytesValue()))
        .setJsonArgs("[]")
        .build();
    final Event converted = converter.convertToDomainModel(rpcEvent);
    assertNotNull(converted);
  }

}
