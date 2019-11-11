/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.WalletException;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class SendIT extends AbstractWalletApiIT {

  @Test
  public void shouldSendAergo() {
    // when
    walletApi.unlock(authentication);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.GIGA_ONE;
    final Fee fee = Fee.EMPTY;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());
    final TxHash txHash = walletApi.transactionApi().send(recipient, amount, fee, payload);
    waitForNextBlockToGenerate();

    // then
    final AccountState actual = walletApi.queryApi().getAccountState(recipient);
    assertEquals(amount, actual.getBalance());
    final Transaction transaction = walletApi.queryApi().getTransaction(txHash);
    assertEquals(payload, transaction.getPayload());
  }

  @Test
  public void shouldSendAergoFailOnLocked() {
    // when
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.GIGA_ONE;
    final Fee fee = Fee.EMPTY;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());

    // then
    try {
      walletApi.transactionApi().send(recipient, amount, fee, payload);
      fail();
    } catch (WalletException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldSendAergoWithName() {
    // given
    walletApi.unlock(authentication);
    final String name = randomName();
    walletApi.transactionApi().createName(name);
    waitForNextBlockToGenerate();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    walletApi.transactionApi().updateName(name, recipient);
    waitForNextBlockToGenerate();
    walletApi.lock(authentication);

    // when
    walletApi.unlock(authentication);
    final Aer amount = Aer.GIGA_ONE;
    final Fee fee = Fee.EMPTY;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());
    final TxHash txHash = walletApi.transactionApi().send(name, amount, fee, payload);
    waitForNextBlockToGenerate();

    // then
    final AccountState actual = walletApi.queryApi().getAccountState(recipient);
    assertEquals(amount, actual.getBalance());
    final Transaction transaction = walletApi.queryApi().getTransaction(txHash);
    assertEquals(payload, transaction.getPayload());
  }

  @Test
  public void shouldSendAergoWithNameFailOnLocked() {
    // given
    walletApi.unlock(authentication);
    final String name = randomName();
    walletApi.transactionApi().createName(name);
    waitForNextBlockToGenerate();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    walletApi.transactionApi().updateName(name, recipient);
    waitForNextBlockToGenerate();
    walletApi.lock(authentication);

    // when
    final Aer amount = Aer.GIGA_ONE;
    final Fee fee = Fee.EMPTY;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());

    // then
    try {
      walletApi.transactionApi().send(name, amount, fee, payload);
      fail();
    } catch (WalletException e) {
      // good we expected this
    }
  }

}
