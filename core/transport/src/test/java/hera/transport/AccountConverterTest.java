/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import org.junit.Test;
import types.AccountOuterClass;

public class AccountConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Account, AccountOuterClass.Account> converter = new AccountConverterFactory()
        .create();

    final Account domainAccount = new Account();
    final AccountOuterClass.Account rpcAccount = converter.convertToRpcModel(domainAccount);
    final Account actualDomainAccount = converter.convertToDomainModel(rpcAccount);
    assertNotNull(actualDomainAccount);
  }

}
