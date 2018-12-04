/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function4WithIdentity<T1, T2, T3, T4, R>
    implements Function4<T1, T2, T3, T4, R>, WithIdentity {

  protected final Function4<T1, T2, T3, T4, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
    return delegate.apply(t1, t2, t3, t4);
  }

}
