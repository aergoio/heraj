/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;

import hera.Invocation;
import hera.RequestMethod;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class TestInvocation<T> implements Invocation<T> {

  @Getter
  protected final RequestMethod<T> requestMethod;

  @Getter
  protected final List<Object> parameters = emptyList();

  @Override
  public T invoke() throws Exception {
    return requestMethod.invoke();
  }

  @Override
  public Invocation<T> withParameters(final List<Object> parameters) {
    return new TestInvocation<>(requestMethod);
  }

}
