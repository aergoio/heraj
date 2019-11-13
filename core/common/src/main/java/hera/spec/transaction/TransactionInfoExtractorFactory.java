/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ContractInvocation;
import hera.exception.HerajException;

@ApiAudience.Public
@ApiStability.Unstable
public class TransactionInfoExtractorFactory {

  /**
   * Create {@link TransactionInfoExtractor} for a given type.
   *
   * @param <T> an extract target type
   * @param clazz an extract target type
   * @return a extractor
   */
  @SuppressWarnings("unchecked")
  public <T> TransactionInfoExtractor<T> create(final Class<T> clazz) {
    if (clazz.equals(ContractInvocation.class)) {
      return (TransactionInfoExtractor<T>) new ContractInvocationInfoExtractor();
    } else {
      throw new HerajException("Unsupported extract target class: " + clazz.getName());
    }
  }

}
