/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.key.AergoKey;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public interface SignAsyncOperation extends Adaptor, ContextAware {

  /**
   * Sign for transaction asynchronously.
   *
   * @param key key to sign
   * @param transaction transaction to sign
   * @return future of signing result or error
   */
  ResultOrErrorFuture<Signature> sign(AergoKey key, Transaction transaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param key key to verify
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(AergoKey key, Transaction transaction);


}
