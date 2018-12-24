/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.highlevel;

import hera.contract.SmartContract;

public interface SmartContractInterface extends SmartContract {

  void set(final String key, final int intVal, final String stringVal);

  Data get(final String key);

}
