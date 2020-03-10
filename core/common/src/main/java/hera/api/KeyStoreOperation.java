/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.List;

/**
 * Provide server keystore related operations. It provides followings:
 *
 * <ul>
 * <li>create / lookup stored accounts</li>
 * <li>locking / unlocking account</li>
 * <li>sign raw transaction with unlocked account</li>
 * <li>importing / exporting account</li>
 * </ul>
 *
 * @author Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface KeyStoreOperation {

  /**
   * Get account list on a key store.
   *
   * @return account list
   */
  List<AccountAddress> list();

  /**
   * Create an account with a password. The private key is stored in a server key store.
   *
   * @param password a password to encrypt private key
   * @return created account
   */
  AccountAddress create(String password);

  /**
   * Lock a private key of account which is stored in a server key store.
   *
   * @param authentication an authentication to lock
   * @return lock result
   */
  boolean lock(Authentication authentication);

  /**
   * Unlock a private key of account which is stored in a server key store.
   *
   * @param authentication an authentication to unlock
   * @return unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Sign for transaction. A sender of transaction should be unlocked.
   *
   * @param rawTransaction raw transaction to sign
   * @return signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Import an encrypted private key to a server key store. An {@code oldPassword} is used to
   * decrypt passed private key. An {@code newPassword} is used to store private key as encrypted
   * state in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param oldPassword an old password to decrypt encrypted private key
   * @param newPassword an new password to encrypt passed private key in a server keystore
   * @return an imported account
   */
  AccountAddress importKey(EncryptedPrivateKey encryptedKey, String oldPassword,
      String newPassword);

  /**
   * Export an encrypted private key of account which is stored in a server key store. An encrypt
   * password is set from authentication
   *
   * @param authentication an authentication of stored account
   * @return an encrypted private key
   */
  EncryptedPrivateKey exportKey(Authentication authentication);

  /**
   * Send transaction. This method automatically fill nonce, sign and commit in a server. This
   * method is valid only if sender is stored in a server key store. Make sure that {@code sender}
   * is unlocked.
   *
   * @param sender aergo sender
   * @param recipient aergo recipient
   * @param amount aergo amount
   * @param payload a payload
   * @return a transaction hash
   */
  TxHash send(AccountAddress sender, AccountAddress recipient, Aer amount, BytesValue payload);

}
