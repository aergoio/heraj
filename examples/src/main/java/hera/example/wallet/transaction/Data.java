/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

public class Data {

  protected int intVal;

  protected String stringVal;

  public int getIntVal() {
    return intVal;
  }

  public void setIntVal(int intVal) {
    this.intVal = intVal;
  }

  public String getStringVal() {
    return stringVal;
  }

  public void setStringVal(String stringVal) {
    this.stringVal = stringVal;
  }

  @Override
  public String toString() {
    return "Data [intVal=" + intVal + ", stringVal=" + stringVal + "]";
  }

}
