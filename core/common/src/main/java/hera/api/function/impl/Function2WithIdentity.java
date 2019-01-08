/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function.impl;

import hera.api.function.Function2;
import hera.api.function.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function2WithIdentity<T1, T2, R> implements Function2<T1, T2, R>, WithIdentity {

  protected final Function2<T1, T2, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T1 t1, final T2 t2) {
    return delegate.apply(t1, t2);
  }

}
