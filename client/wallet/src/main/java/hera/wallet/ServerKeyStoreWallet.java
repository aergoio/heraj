/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.TryCountAndInterval;
import hera.client.AergoClient;
import java.security.KeyStore;

public class ServerKeyStoreWallet extends AbstractWalletWithKeyStore {

  ServerKeyStoreWallet(final AergoClient aergoClient,
      final TryCountAndInterval nonceRefreshTryCount) {
    super(aergoClient, nonceRefreshTryCount);
    this.keyStore = new ServerKeyStoreAdaptor(this.aergoClient);
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    // do nothing
  }

}
