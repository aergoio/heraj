/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import hera.contract.SmartContract;

public interface SmartContractSample extends SmartContract {

  void setNil(Object nilArg);

  Object getNil();

  void setBoolean(boolean booleanArg);

  boolean getBoolean();

  void setNumber(int numberArg);

  int getNumber();

  void setString(String stringArg);

  String getString();

}
