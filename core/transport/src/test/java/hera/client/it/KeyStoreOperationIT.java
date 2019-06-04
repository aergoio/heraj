/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class KeyStoreOperationIT extends AbstractIT {

  protected final Aer amount = Aer.of("100", Unit.GAER);

  protected boolean unlock(final AccountAddress accountAddress, final String password) {
    return aergoClient.getKeyStoreOperation().unlock(Authentication.of(accountAddress, password));
  }

  protected boolean lock(final AccountAddress accountAddress, final String password) {
    return aergoClient.getKeyStoreOperation().lock(Authentication.of(accountAddress, password));
  }

  @Test
  public void testUnlockOnInvalidIdentity() {

    final Identity identity = new Identity() {

      @Override
      public String getValue() {
        return randomUUID().toString();
      }
    };
    final Authentication authentication = new Authentication(identity, randomUUID().toString());
    boolean unlockResult = aergoClient.getKeyStoreOperation().unlock(authentication);
    assertFalse(unlockResult);
  }

  @Test
  public void testCreateAndExport() {
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    assertTrue(aergoClient.getKeyStoreOperation().list().contains(created));

    final Authentication authentication = Authentication.of(created, password);
    final EncryptedPrivateKey exported =
        aergoClient.getKeyStoreOperation().exportKey(authentication);
    final AergoKey key = AergoKey.of(exported, password);
    assertEquals(key.getAddress(), created);
  }

  @Test
  public void testCreateAndExportWithInvalidAuthentication() {
    final AccountAddress account = aergoClient.getKeyStoreOperation().create(password);
    assertTrue(aergoClient.getKeyStoreOperation().list().contains(account));

    final Authentication authentication = Authentication.of(account, randomUUID().toString());
    try {
      aergoClient.getKeyStoreOperation().exportKey(authentication);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testImportAndExport() {
    final Account account = supplyLocalAccount();
    final String oldPassword = randomUUID().toString();
    final String newPassword = randomUUID().toString();
    final EncryptedPrivateKey encrypted = account.getKey().export(oldPassword);
    final AccountAddress imported =
        aergoClient.getKeyStoreOperation().importKey(encrypted, oldPassword, newPassword);

    assertTrue(aergoClient.getKeyStoreOperation().list().contains(account.getAddress()));
    assertEquals(account.getAddress(), imported);

    final Authentication authentication = Authentication.of(imported, newPassword);
    final EncryptedPrivateKey exported =
        aergoClient.getKeyStoreOperation().exportKey(authentication);
    final AergoKey key = AergoKey.of(exported, newPassword);
    assertEquals(account.getAddress(), key.getAddress());
  }

  @Test
  public void testSignOnLockedKeyStoreAccount() {
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(created)
            .to(recipient)
            .amount(amount)
            .nonce(1L)
            .build();

    // unlock(account, password);
    try {
      aergoClient.getKeyStoreOperation().sign(rawTransaction);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testSendWithKeyStoreAccount() {
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    fund(created);

    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    // snapshot pre state
    final AccountState preState = aergoClient.getAccountOperation().getState(created);

    unlock(created, password);
    final TxHash txHash = aergoClient.getTransactionOperation().send(created, recipient, amount);
    lock(created, password);

    final Transaction notConfirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertTrue(false == notConfirmed.isConfirmed());

    waitForNextBlockToGenerate();

    final Transaction confirmed = aergoClient.getTransactionOperation().getTransaction(txHash);
    final AccountState refreshed = aergoClient.getAccountOperation().getState(created);
    assertTrue(true == confirmed.isConfirmed());
    verifyState(preState, refreshed);
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
  }

  @Test
  public void testSendOnLockedKeyStoreAccount() {
    final String password = randomUUID().toString();
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);

    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    try {
      // unlock(account, password);
      aergoClient.getTransactionOperation().send(created, recipient, amount);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
