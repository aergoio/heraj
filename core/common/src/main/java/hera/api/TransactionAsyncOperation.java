/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TransactionAsyncOperation {

  /**
   * Get transaction asynchronously.
   *
   * @param hash transaction's hash
   * @return transaction
   */
  CompletableFuture<Optional<Transaction>> getTransaction(Hash hash);

  /**
   * Sign for transaction asynchronously.
   *
   * @param transaction transaction to sign
   * @return signing result
   */
  CompletableFuture<Signature> sign(Transaction transaction);


  /**
   * Verify transaction asynchronously.
   *
   * @param transaction transaction to verify
   * @return verify result
   */
  CompletableFuture<Boolean> verify(Transaction transaction);

  /**
   * Commit transaction asynchronously.
   *
   * @param transaction transaction to commit
   * @return transaction hash
   */
  CompletableFuture<Optional<Hash>> commit(Transaction transaction);

}
