/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet;

import hera.contract.SmartContract;

public interface SmartContractInterface extends SmartContract {

  void set(String key, int intVal, String stringVal);

  Data get(String key);

}
