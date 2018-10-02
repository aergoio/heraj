/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import org.junit.Test;
import types.Rpc;

public class AuthenticationConverterTest extends AbstractTestCase {

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.ADDRESS_VERSION}));

  @Test
  public void testConvert() {
    final ModelConverter<Authentication, Rpc.Personal> converter =
        new AuthenticationConverterFactory().create();

    final Rpc.Personal rpcAuthentication =
        converter.convertToRpcModel(Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString()));

    assertNotNull(rpcAuthentication);
  }

}
