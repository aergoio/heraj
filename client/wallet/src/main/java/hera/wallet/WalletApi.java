/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Authentication;
import hera.api.model.Identity;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.Signer;
import java.util.List;

public interface WalletApi extends Signer {

  void use(AergoClient aergoClient);

  void bind(java.security.KeyStore keyStore);

  boolean unlock(Authentication authentication);

  boolean lock(Authentication authentication);

  void save(Authentication authentication, AergoKey key);

  String export(Authentication authentication);

  List<Identity> listIdentities();

  TransactionApi transactionApi();

  QueryApi queryApi();

}
