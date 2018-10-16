/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.SignLocalTemplate;
import hera.api.SignOperation;

public class LocalSignStrategy implements SignStrategy<Object> {

  @Override
  public SignOperation getSignOperation() {
    return new SignLocalTemplate();
  }

}
