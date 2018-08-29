/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrErrorFuture;

public interface ContractAsyncOperation {

  /**
   * Get receipt of transaction asynchronously.
   *
   * @param hash transaction hash
   * @return future of receipt of transaction or error
   */
  ResultOrErrorFuture<Receipt> getReceipt(Hash hash);
}
