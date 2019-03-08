/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import static java.util.UUID.randomUUID;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.key.AergoKey;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;

public abstract class AbstractExample {

  protected String hostname = "localhost:7845";

  protected String richEncrypted =
      "47VYaB9WJ4FoBaiZ1HAh3yDBkSFDVM3fVxgjTfAnmmFa8GqyAZUb4gqKKirkoRgdhMazRHzbR";

  protected String richPassword = "password";

  protected void sleep(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected void fund(final AccountAddress accountAddress) {
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .build(WalletType.Naive);

    final AergoKey richKey = new AergoKey(richEncrypted, richPassword);
    final String password = randomUUID().toString();
    wallet.saveKey(richKey, password);

    final Authentication authentication = new Authentication(richKey.getAddress(), password);
    wallet.unlock(authentication);

    wallet.send(accountAddress, Aer.of("100", Unit.AERGO), Fee.getDefaultFee());
    sleep(1200L);

    wallet.close();
  }

  public abstract void run() throws Exception;

}
