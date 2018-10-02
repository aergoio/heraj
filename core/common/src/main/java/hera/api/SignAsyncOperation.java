/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;

@ApiAudience.Public
@ApiStability.Unstable
public interface SignAsyncOperation {

  /**
   * Sign for transaction asynchronously.
   *
   * @param transaction transaction to sign
   * @return future of signing result or error
   */
  ResultOrErrorFuture<Signature> sign(Transaction transaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(Transaction transaction);


}
