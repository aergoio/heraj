/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;

public interface ContractOperation {

  /**
   * Get receipt of transaction.
   *
   * @param hash transaction hash
   * @return receipt of transaction or error
   */
  ResultOrError<Receipt> getReceipt(Hash hash);
}
