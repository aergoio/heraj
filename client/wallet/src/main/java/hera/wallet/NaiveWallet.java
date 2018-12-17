/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import java.security.KeyStore;

public class NaiveWallet extends InteractiveWallet {

  NaiveWallet(final AergoClient aergoClient, final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient, tryCountAndInterval);
    this.keyStore = new InMemoryKeyStore();
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    // do nothing
  }

}
