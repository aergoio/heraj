/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.keystore.JavaKeyStore;
import java.security.KeyStore;

@ApiAudience.Public
@ApiStability.Unstable
public class SecureWallet extends AbstractWallet {

  SecureWallet(final AergoClient aergoClient, final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient, tryCountAndInterval);
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    this.keyStore = new JavaKeyStore(keyStore);
  }

}
