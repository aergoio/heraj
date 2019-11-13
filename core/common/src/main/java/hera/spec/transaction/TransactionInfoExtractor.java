/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface TransactionInfoExtractor<T> {

  T extract(Transaction transaction);

}
