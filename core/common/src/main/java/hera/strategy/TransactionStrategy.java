/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.model.Transaction;

public interface TransactionStrategy {
  void sendTransaction(Transaction transaction);
}
