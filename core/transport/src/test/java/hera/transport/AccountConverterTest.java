/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.internal.ServerManagedAccount;
import org.junit.Test;
import types.AccountOuterClass;

public class AccountConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ServerManagedAccount, AccountOuterClass.Account> converter =
        new AccountConverterFactory().create();

    final ServerManagedAccount domainAccount = new ServerManagedAccount(
        AccountAddress.of(BytesValue.of(new byte[] {AccountAddress.VERSION})));
    
    final AccountOuterClass.Account rpcAccount = converter.convertToRpcModel(domainAccount);
    final ServerManagedAccount actualDomainAccount = converter.convertToDomainModel(rpcAccount);
    assertNotNull(actualDomainAccount);
  }

}
