/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.key.AergoKey;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public interface SignEitherOperation extends Adaptor, ContextAware {

  /**
   * Sign for transaction.
   *
   * @param key key to sign
   * @param transaction transaction to sign
   * @return signing result or error
   */
  ResultOrError<Signature> sign(AergoKey key, Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param key key to verify
   * @param transaction transaction to verify
   * @return verify result or error
   */
  ResultOrError<Boolean> verify(AergoKey key, Transaction transaction);

}
