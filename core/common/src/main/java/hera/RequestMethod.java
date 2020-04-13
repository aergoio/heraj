/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import hera.exception.HerajException;
import java.util.List;

public abstract class RequestMethod<T> {

  public T run(final List<Object> parameters) throws Exception {
    final String message = validate(parameters);
    if (null != message) {
      throw new HerajException(message);
    }
    return runInternal(parameters);
  }

  public Invocation<T> toInvocation(final List<Object> parameters) {
    assertNotNull(parameters);
    return new PlainInvocation<>(this, parameters);
  }

  public abstract String getName();

  protected abstract String validate(final List<Object> parameters);

  protected abstract T runInternal(final List<Object> parameters) throws Exception;

}
