/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.client.AergoClient;
import java.security.KeyStore;

public class ServerKeyStoreWallet extends AbstractWalletWithKeyStore {

  ServerKeyStoreWallet(final AergoClient aergoClient, int nonceRefreshCount) {
    super(aergoClient, nonceRefreshCount);
    this.keyStore = new ServerKeyStoreAdaptor(this.aergoClient);
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    // do nothing
  }

}
