/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import org.junit.Test;
import types.Blockchain.State;

public class AccountStateConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<AccountState, State> converter = new AccountStateConverterFactory()
        .create();

    final AccountState domainAccountState = new AccountState();
    domainAccountState.setAddress(AccountAddress.of(randomUUID().toString().getBytes()));
    final State rpcAccountState = converter.convertToRpcModel(domainAccountState);
    final AccountState actualDomainAccountState = converter.convertToDomainModel(rpcAccountState);
    assertNotNull(actualDomainAccountState);
  }

}
