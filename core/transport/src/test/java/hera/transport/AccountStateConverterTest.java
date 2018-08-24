/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Account;
import org.junit.Test;
import types.Blockchain;

public class AccountStateConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Account, Blockchain.State> converter =
        new AccountStateConverterFactory().create();

    final Blockchain.State rpcAccountState = Blockchain.State.newBuilder().build();
    final Account domainAccount = converter.convertToDomainModel(rpcAccountState);
    assertNotNull(domainAccount);
  }

}
