/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.WithIdentity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function1WithIdentity<T, R> implements Function1<T, R>, WithIdentity {

  protected final Function1<T, R> delegate;

  @Getter
  protected final String identity;

  @Override
  public R apply(final T t) {
    return delegate.apply(t);
  }

}
