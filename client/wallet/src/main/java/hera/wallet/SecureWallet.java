/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import java.security.KeyStore;

public class SecureWallet extends InteractiveWallet {

  SecureWallet(final AergoClient aergoClient, final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient, tryCountAndInterval);
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    this.keyStore = new JavaKeyStore(keyStore);
  }

}
