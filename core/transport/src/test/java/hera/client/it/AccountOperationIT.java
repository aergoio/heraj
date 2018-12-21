/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  @Test
  public void testCreateAndGetName() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

      final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

      unlockAccount(account, password);
      aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
          account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(passed.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));
    }
  }

  @Test
  public void testCreateWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().createName(account, name, account.getNonce());
        fail();
      } catch (Exception e) {
        // good we expected this
      }
      lockAccount(account, password);
    }
  }

  @Test
  public void testUpdateWithInvalidNonce() {
    for (final Account account : supplyAccounts()) {
      final String name = randomUUID().toString().substring(0, 12).replace('-', 'a');

      unlockAccount(account, password);
      aergoClient.getAccountOperation().createName(account, name, account.incrementAndGetNonce());
      lockAccount(account, password);

      waitForNextBlockToGenerate();

      assertEquals(account.getAddress(), aergoClient.getAccountOperation().getNameOwner(name));

      final Account passed = new AccountFactory().create(new AergoKeyGenerator().create());

      unlockAccount(account, password);
      try {
        aergoClient.getAccountOperation().updateName(account, name, passed.getAddress(),
            account.getNonce());
        fail();
      } catch (Exception e) {
        // good we expected this
      }
      lockAccount(account, password);
    }
  }

}
