/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNull;

import hera.api.AergoEitherApi;
import hera.api.TransactionEitherOperation;
import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.Reader;

public class NaiveWallet implements Wallet {

  public static Wallet newWallet() throws Exception {
    return newWallet(null);
  }

  /**
   * Create new wallet.
   *
   * @param context context to bind to wallet.
   *
   * @return wallet to be created
   *
   * @throws Exception failure on creation
   */
  public static Wallet newWallet(final Context context) throws Exception {
    final NaiveWallet wallet = new NaiveWallet();
    wallet.keyPair = new ECDSAKeyGenerator().create();
    wallet.bind(context);
    return wallet;
  }

  public static Wallet importWallet(final byte[] encodedPrivateKey) throws Exception {
    return importWallet(encodedPrivateKey, null);
  }

  /**
   * Import a wallet from private key.
   *
   * @param encodedPrivateKey encoded private key
   * @param context wallet to be created
   *
   * @return wallet to be imported
   *
   * @throws Exception failure on import
   */
  public static Wallet importWallet(final byte[] encodedPrivateKey, final Context context)
      throws Exception {
    final NaiveWallet wallet = new NaiveWallet();
    wallet.keyPair = new ECDSAKeyGenerator().create(encodedPrivateKey);
    return wallet;
  }

  protected Context context;

  protected ECDSAKey keyPair;

  @Override
  public void bind(final Context context) {
    assertNull(this.context, "The context already bound");
    this.context = context;
  }

  public void createAccount(final String password) {
  }

  public void unlock(Object authentication) {
  }

  public void lock() {
  }

  /**
   * Send transaction.
   *
   * @param transaction transaction to send
   */
  public void sendTransaction(final Transaction transaction) {
  }

  /**
   * Define contract.
   *
   * @param address address to register to
   * @param reader contract content reader
   */
  public void defineContract(final String address, final Reader reader) {
  }

  /**
   * Execute contract.
   *
   * @param address address to execute
   */
  public void executeContract(final String address) {

  }

  /**
   * Query contract.
   *
   * @param address address to query
   * @param args contract's arguments
   *
   * @return query result
   */
  public Object queryContract(final String address, Object... args) {
    return null;
  }
}
