/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import java.util.Optional;

public interface TransactionOperation {

  /**
   * Get transaction.
   *
   * @param hash transaction's hash
   * @return transaction
   */
  Optional<Transaction> getTransaction(Hash hash);

  /**
   * Sign for transaction.
   *
   * @param transaction transaction to sign
   * @return signing result
   */
  Signature sign(Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param transaction transaction to verify
   * @return verify result
   */
  boolean verify(Transaction transaction);

  /**
   * Commit transaction.
   *
   * @param transaction transaction to commit
   * @return transaction hash
   */
  Optional<Hash> commit(Transaction transaction);

}
