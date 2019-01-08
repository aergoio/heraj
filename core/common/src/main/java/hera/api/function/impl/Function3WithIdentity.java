/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function.impl;

import hera.api.function.Function3;
import hera.api.function.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function3WithIdentity<T1, T2, T3, R>
    implements Function3<T1, T2, T3, R>, WithIdentity {

  protected final Function3<T1, T2, T3, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T1 t1, final T2 t2, final T3 t3) {
    return delegate.apply(t1, t2, t3);
  }

}
