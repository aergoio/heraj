/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Function1WithIdentity<T, R> implements Function1<T, R>, WithIdentity {

  protected final Function1<T, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T t) {
    return delegate.apply(t);
  }

}
