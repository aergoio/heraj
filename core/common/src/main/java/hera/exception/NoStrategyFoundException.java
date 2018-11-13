/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoStrategyFoundException extends HerajException {

  private static final long serialVersionUID = -6795798021488987186L;

  protected final Class<?> strategyClass;

  @Override
  public String getLocalizedMessage() {
    return String.format("No strategy of type %s", strategyClass.toString());
  }

}
