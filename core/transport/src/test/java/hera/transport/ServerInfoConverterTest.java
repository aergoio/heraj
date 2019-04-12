/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ServerInfo;
import org.junit.Test;
import types.Rpc;

public class ServerInfoConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final Rpc.ServerInfo rawServerInfo = Rpc.ServerInfo.newBuilder().build();
    final ModelConverter<ServerInfo, Rpc.ServerInfo> converter =
        new ServerInfoConverterFactory().create();
    final ServerInfo actualDomainServerInfo = converter.convertToDomainModel(rawServerInfo);
    assertNotNull(actualDomainServerInfo);
  }

}