/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Authentication;
import org.junit.Test;
import types.Rpc;

public class AuthenticationConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Authentication, Rpc.Personal> converter =
        new AuthenticationConverterFactory().create();

    final Rpc.Personal rpcAuthentication = converter.convertToRpcModel(anyAuthentication);

    assertNotNull(rpcAuthentication);
  }

}
