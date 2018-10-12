/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Context;
import hera.api.SignLocalTemplate;
import hera.api.SignOperation;

public class LocalSignStrategy implements SignStrategy<Object> {

  @Override
  public SignOperation getSignOperation(final Object connection, final Context context) {
    return new SignLocalTemplate(context);
  }

}
