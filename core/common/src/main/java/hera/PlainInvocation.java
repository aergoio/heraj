/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PlainInvocation<T> implements Invocation<T> {

  @Getter
  protected final RequestMethod<T> requestMethod;

  @Getter
  protected final List<Object> parameters;

  @Override
  public T invoke() throws Exception {
    return requestMethod.run(parameters);
  }

  @Override
  public Invocation<T> withParameters(final List<Object> parameters) {
    assertNotNull(parameters, "Parameters must not null");
    return new PlainInvocation<>(requestMethod, parameters);
  }
}
