/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function.impl;

import hera.api.function.Function5;
import hera.api.function.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function5WithIdentity<T1, T2, T3, T4, T5, R>
    implements Function5<T1, T2, T3, T4, T5, R>, WithIdentity {

  protected final Function5<T1, T2, T3, T4, T5, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
    return delegate.apply(t1, t2, t3, t4, t5);
  }

}
