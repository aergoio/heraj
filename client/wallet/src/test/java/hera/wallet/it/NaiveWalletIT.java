/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static java.util.UUID.randomUUID;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class NaiveWalletIT extends AbstractIT {

  @Test
  public void test() throws InterruptedException {
    Wallet wallet = new WalletBuilder()
        .withNonBlockingConnect()
        .withEndpoint("localhost:7845")
        .withRetry(2, 1, TimeUnit.SECONDS)
        .build(WalletType.ServerKeyStore);

    final String password = randomUUID().toString();
    final AergoKey key = new AergoKeyGenerator().create();
    wallet.saveKey(key, password);
    Authentication authentication = Authentication.of(key.getAddress(), password);
    wallet.unlock(authentication);

//    System.out.println(wallet.getAccountState());
//    RawTransaction rawTransaction = RawTransaction.newBuilder()
//        .sender(wallet.getAddress())
//        .recipient(wallet.getAddress())
//        .amount("100")
//        .nonce(1L)
//        .build();
//    Transaction signed = wallet.sign(rawTransaction);

    System.out.println(wallet.getRecentlyUsedNonce());
    AccountAddress recipient =
        AccountAddress.of(() -> "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");
    System.out.println(wallet.getAccountState(recipient));
    wallet.send(recipient, "1000", Fee.getDefaultFee());
    wallet.send(recipient, "1000", Fee.getDefaultFee());
    Thread.sleep(1500L);
    System.out.println(wallet.getAccountState(recipient));
    System.out.println(wallet.getRecentlyUsedNonce());

    // TxHash fuck = wallet.commit(rawTransaction);
    // TxHash suck = wallet.commit(signed);

    // System.out.println(fuck);
    // System.out.println(suck);

    // System.out.println(wallet.getTransaction(fuck));
    // System.out.println(wallet.getTransaction(suck));
  }

}
