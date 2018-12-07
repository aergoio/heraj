/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountState;
import org.junit.Test;
import types.Blockchain;

public class AccountStateConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<AccountState, Blockchain.State> converter =
        new AccountStateConverterFactory().create();

    final Blockchain.State rpcAccountState = Blockchain.State.newBuilder().setNonce(1L).build();
    final AccountState domainAccountState = converter.convertToDomainModel(rpcAccountState);
    assertNotNull(domainAccountState);
  }

}
