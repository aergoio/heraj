/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import java.security.KeyStore;

@ApiAudience.Public
@ApiStability.Unstable
public class ServerKeyStoreWallet extends InteractiveWallet {

  ServerKeyStoreWallet(final AergoClient aergoClient,
      final TryCountAndInterval nonceRefreshTryCount) {
    super(aergoClient, nonceRefreshTryCount);
    this.keyStore = new ServerKeyStore(getAergoClient());
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    // do nothing
  }

}
